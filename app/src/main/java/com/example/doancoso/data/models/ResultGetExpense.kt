package com.example.doancoso.data.models

data class ResultGetExpense(
    val category: String,
    val amount: Double,
    val percent: Double,
    val type: String,
    val total: Double,
    val color: Long
)