package com.example.doancoso.data.repository

import com.example.doancoso.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

open class AuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    suspend fun register(name: String, email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false
            val user = User(uid, name, email)
            database.child(uid).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return false
            val uid = user.uid
            val name = user.displayName ?: "No Name"
            val email = user.email ?: return false
            val newUser = User(uid, name, email)
            database.child(uid).setValue(newUser).await()
            true
        } catch (e: Exception) {
            println("Lỗi khi đăng nhập với Google: ${e.message}")
            false
        }
    }

    fun registerUser(email: String, password: String, name: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        onComplete(false, "Không lấy được UID")
                        return@addOnCompleteListener
                    }
                    val user = User(uid, name, email)
                    database.child(uid).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            onComplete(dbTask.isSuccessful, dbTask.exception?.message)
                        }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?, User?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        onComplete(false, "Không có UID", null)
                        return@addOnCompleteListener
                    }
                    fetchUser(uid) { user ->
                        if (user != null) {
                            onComplete(true, null, user)
                        } else {
                            onComplete(false, "Không tìm thấy người dùng", null)
                        }
                    }
                } else {
                    onComplete(false, task.exception?.message, null)
                }
            }
    }

    private fun fetchUser(uid: String, onComplete: (User?) -> Unit) {
        database.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(User::class.java)
                onComplete(user)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun logout() {
        auth.signOut()
    }
}
