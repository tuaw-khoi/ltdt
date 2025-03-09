package com.example.doancoso.presentation.ui

import androidx.lifecycle.ViewModel
import com.example.doancoso.data.models.User
import com.example.doancoso.data.repository.AuthService

class AuthViewModel : ViewModel() {
    private val authService = AuthService()

    fun registerUser(email: String, password: String, name: String) {

    }

    fun loginUser(email: String, password: String) {

    }

    fun resetAuthState() {

    }

    fun logoutUser() {

    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class UserLoggedIn(val user: User?) : AuthState()
    data class Error(val message: String) : AuthState()
}
