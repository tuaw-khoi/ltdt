package com.example.doancoso.data.repository

import com.example.doancoso.data.models.User

class AuthService {

    fun registerUser(email: String, password: String, name: String, onComplete: (Boolean, String?) -> Unit) {

    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?, User?) -> Unit) {

    }

    private fun fetchUser(uid: String, onComplete: (User?) -> Unit) {

    }

    fun logout() {

    }
}
