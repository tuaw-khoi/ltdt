package com.example.doancoso.data.models

data class DailyExpense(
    var date: String = "",  // Format: "yyyy-MM-dd"
    var expenses: List<ExpenseItem> = emptyList()
)
