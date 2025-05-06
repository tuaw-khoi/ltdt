package com.example.doancoso.data.repository



import com.example.doancoso.data.models.ExpenseItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ExpenseItemService {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("expenses")

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun addExpense(expense: ExpenseItem): Boolean {
        val uid = getCurrentUserId() ?: return false
        return try {
            val key = database.child(uid).push().key ?: return false
            database.child(uid).child(key).setValue(expense).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getExpenses(): List<ExpenseItem> {
        val uid = getCurrentUserId() ?: return emptyList()
        return try {
            val snapshot = database.child(uid).get().await()
            val list = mutableListOf<ExpenseItem>()
            for (child in snapshot.children) {
                val expense = child.getValue(ExpenseItem::class.java)
                if (expense != null) {
                    list.add(expense)
                }
            }
            list
        } catch (e: Exception) {
            emptyList()
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
