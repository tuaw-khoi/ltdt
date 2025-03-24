package com.example.doancoso.data.repository

import com.example.doancoso.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    suspend fun register(name: String, email : String , password : String ): Boolean{
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
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
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            println("Lỗi khi đăng nhập với Google: ${e.message}")
            false
        }
    }
    fun registerUser(email: String, password: String, name: String, onComplete: (Boolean, String?) -> Unit) {

    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?, User?) -> Unit) {

    }

    private fun fetchUser(uid: String, onComplete: (User?) -> Unit) {

    }

    fun logout() {
        auth.signOut()

    }

}
