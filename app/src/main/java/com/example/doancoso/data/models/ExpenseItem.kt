package com.example.doancoso.data.models

data class ExpenseItem(
    val date: String = "", // "2025-05-07"
    val timestamp: Long = 0L, // System.currentTimeMillis()
    val category: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val type: String = "" // "Thu nhập" hoặc "Chi phí"
)

data class ExpenseItemHistory(
    val id: String = "",
    val date: String = "", // ví dụ: "2025-05-07"
    val timestamp: Long = 0L,
    val category: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val type: String = "" // "Thu nhập" hoặc "Chi phí"
)

data class GroupedTransaction(
    val date: String,
    val total: Double,
    val type: String, // "Thu nhập" hoặc "Chi phí"
    val transactions: List<ExpenseItemHistory>
)

