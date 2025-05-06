package com.example.doancoso.data.models

data class BudgetPlan(
    var uid: String? = null,  // Firebase key
    var title: String = "",
    var createdDate: String = "",  // yyyy-MM-dd
    var dailyExpenses: List<DailyExpense> = emptyList()
)
