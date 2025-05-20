package com.example.doancoso.domain

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// Định nghĩa một lớp Budget để chứa thông tin ngân sách
data class Budget(
    val category: String,
    val amount: Double,
    val duration: Int // Số ngày áp dụng
)

class BudgetViewModel : ViewModel() {

    // Danh sách lưu trữ các ngân sách
    private val _budgetList = mutableListOf<Budget>()

    // MutableState để theo dõi danh sách ngân sách (sử dụng cho giao diện)
    val budgetList = mutableStateOf(_budgetList)

    // Phương thức để thêm ngân sách
    fun addBudget(category: String, amount: Double, duration: Int) {
        val newBudget = Budget(category, amount, duration)
        _budgetList.add(newBudget)
        // Cập nhật lại giá trị của mutableState
        budgetList.value = _budgetList
    }

    // Có thể thêm các phương thức khác như cập nhật ngân sách, xóa ngân sách, v.v.
}
