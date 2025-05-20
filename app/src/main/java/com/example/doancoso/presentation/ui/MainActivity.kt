package com.example.doancoso.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ExpenseItemService
import com.example.doancoso.ui.theme.DoancosoTheme

class MainActivity : ComponentActivity() {
    private var authService = AuthService()
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var expenseItemService: ExpenseItemService // <-- Thêm dòng khai báo này

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthService()
        googleSignInHelper = GoogleSignInHelper(this, authService)
        expenseItemService = ExpenseItemService()  // <-- Khởi tạo ExpenseItemService
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            DoancosoTheme {
                AppNavigation(
                    expenseItemService = expenseItemService,  // <-- Truyền expenseItemService
                    navController = navController,
                    authService = authService,
                    googleSignInClient = googleSignInHelper.getGoogleSignInClient(),
                    signIn = { googleSignInHelper.signInWithGoogle() }
                )
            }
        }
    }
}


