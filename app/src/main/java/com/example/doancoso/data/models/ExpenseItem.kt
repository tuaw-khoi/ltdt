package com.example.doancoso.data.models

data class ExpenseItem(
    var date: String = "",
    var category: String = "",
    var amount: Double = 0.0,
    var note: String = "",
    var type: String = "" // Thêm trường type
)