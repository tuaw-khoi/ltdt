package com.example.doancoso.presentation.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavHostController
import com.example.doancoso.data.repository.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleSignInHelper(
    private val activity: ComponentActivity,
    private val authController: AuthService

) {
    private val googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController
    private val signInLauncher: ActivityResultLauncher<Intent>

    init {
        // Configure Google Sign-In với Web Client ID từ Firebase Console
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("236799263101-klom889v0rinffapjalrf0s3hjfgnn5r.apps.googleusercontent.com") // Thay bằng Web Client ID thực tế
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        // Initialize ActivityResultLauncher
        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                println("Google Sign-In failed with result code: ${result.resultCode}")
            }
        }
    }

    fun setNavController(controller: NavHostController) {
        this.navController = controller
    }

    fun getGoogleSignInClient(): GoogleSignInClient = googleSignInClient

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                // Gọi hàm signInWithGoogle trong Coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    val success = authController.signInWithGoogle(token)
                    if (success) {
                        // Điều hướng đến màn hình chính sau khi đăng nhập thành công
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true

                        }
                    } else {
                        println("Firebase authentication with Google failed")
                    }
                }
            } ?: println("ID Token is null")
        } catch (e: ApiException) {
            println("Google Sign-In failed: ${e.statusCode} - ${e.message}")
        }
    }
}
