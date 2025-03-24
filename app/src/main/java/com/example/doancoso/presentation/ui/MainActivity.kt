package com.example.doancoso.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.ui.theme.DoancosoTheme

class MainActivity : ComponentActivity() {
    private  var  authService = AuthService()
    private lateinit var googleSignInHelper: GoogleSignInHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthService()
        googleSignInHelper = GoogleSignInHelper(this, authService)
        enableEdgeToEdge()
        setContent {
            val navController= rememberNavController()
            DoancosoTheme { // Đảm bảo gọi đúng chủ đề
                AppNavigation(navController ,authService,googleSignInClient = googleSignInHelper.getGoogleSignInClient(),  signIn = { googleSignInHelper.signInWithGoogle() })
            }
        }
    }
}

