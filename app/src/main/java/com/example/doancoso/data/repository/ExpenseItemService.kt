package com.example.doancoso.data.repository

import android.util.Log
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.models.ResultGetExpense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Random

class ExpenseItemService {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("expenses")
    private val random = Random()

    private fun generateRandomColor(): Long {
        return Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255).toArgb().toLong()
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    suspend fun addExpense(expense: ExpenseItem): Boolean {
        val uid = getCurrentUserId() ?: return false
        return try {
            val key = database.child(uid).push().key ?: return false
            database.child(uid).child(key).setValue(expense).await()

            getAllExpenses()
            true
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getAllExpenses(): List<ExpenseItem> {
        val uid = getCurrentUserId() ?: return emptyList()

        return try {
            val snapshot = database.child(uid).get().await()
            val expenseItems = mutableListOf<ExpenseItem>()
            for (child in snapshot.children) {
                val expense = child.getValue(ExpenseItem::class.java)
                expense?.let { expenseItems.add(it) }
            }
            expenseItems
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getExpensesByType(type: String): List<ResultGetExpense> {
        val allExpenses = getAllExpenses()

        Log.d("DEBUG_TYPE", "All Expenses:")
        allExpenses.forEach {
            Log.d("DEBUG_TYPE", "type=${it.type}, category=${it.category}, amount=${it.amount}")
        }

        val filteredExpenses = allExpenses.filter { it.type.equals(type, ignoreCase = true) }

        if (filteredExpenses.isEmpty()) {
            return emptyList()
        }

        val totalAmount = filteredExpenses.sumOf { it.amount }
        val categoryExpenses = filteredExpenses.groupBy { it.category }
        val categoryColors = mutableMapOf<String, Long>()

        return categoryExpenses.map { (category, expenses) ->
            val categoryTotal = expenses.sumOf { it.amount }
            val percent = if (totalAmount > 0) (categoryTotal / totalAmount) * 100 else 0.0
            val color = categoryColors.getOrPut(category) { generateRandomColor() }

            Log.d("ExpenseService", "All: $allExpenses")
            Log.d("ExpenseService", "Filtered($type): $filteredExpenses")

            ResultGetExpense(
                category = category,
                amount = categoryTotal,
                percent = percent,
                type = type,
                total = totalAmount,
                color = color
            )
        }
    }


    suspend fun deleteExpenseByKey(key: String): Boolean {
        val uid = getCurrentUserId() ?: return false
        return try {
            database.child(uid).child(key).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}