package com.example.doancoso.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.models.ExpenseItemHistory
import com.example.doancoso.data.models.GroupedTransaction
import com.example.doancoso.data.models.ResultGetExpense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import java.util.Random

class ExpenseItemService {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("expenses")
    private val random = Random()

    private fun generateRandomColor(): Long {
        return Color(
            red = random.nextInt(256),
            green = random.nextInt(256),
            blue = random.nextInt(256),
            alpha = 255
        ).toArgb().toLong()
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getUserName(): String {
        val uid = getCurrentUserId() ?: return "Chưa xác định"
        return try {
            val snapshot = FirebaseDatabase.getInstance().getReference("users").child(uid).get().await()
            snapshot.child("name").getValue(String::class.java) ?: "Chưa xác định"
        } catch (e: Exception) {
            "Chưa xác định"
        }
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

    suspend fun getAllExpenses(): List<ExpenseItem> {
        val uid = getCurrentUserId() ?: return emptyList()
        return try {
            val snapshot = database.child(uid).get().await()
            val expenseItems = mutableListOf<ExpenseItem>()
            for (child in snapshot.children) {
                val expense = child.getValue(ExpenseItem::class.java)
                Log.d("ExpenseItemService", "Mục chi phí: $expense") // Thêm dòng log này
                expense?.let { expenseItems.add(it) }
            }
            expenseItems
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ✅ Hàm mới: Lấy dữ liệu nhóm theo ngày/tuần/tháng + theo loại
    suspend fun getGroupedExpensesBy(type: String, timeFrame: String): List<ResultGetExpense> {
        val allExpenses = getAllExpenses()
        val formatter = when (timeFrame) {
            "day" -> DateTimeFormatter.ofPattern("dd/MM/yyyy")
            "month" -> DateTimeFormatter.ofPattern("MM/yyyy")
            else -> null
        }
        val grouped = allExpenses.groupBy { expense ->
            val localDate = Instant.ofEpochMilli(expense.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            when (timeFrame) {
                "day" -> localDate.format(formatter!!)
                "month" -> localDate.format(formatter!!)
                "week" -> {
                    val week = localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                    val year = localDate.year
                    "Tuần $week/$year"
                }
                else -> "Unknown"
            }
        }
        val results = mutableListOf<ResultGetExpense>()
        val categoryColors = mutableMapOf<String, Long>()
        for ((timeLabel, items) in grouped) {
            val filteredByType = items.filter { it.type.equals(type, ignoreCase = true) }
            if (filteredByType.isEmpty()) continue
            val totalAmount = filteredByType.sumOf { it.amount }
            val byCategory = filteredByType.groupBy { it.category }
            for ((category, expenses) in byCategory) {
                val amount = expenses.sumOf { it.amount }
                val percent = if (totalAmount != 0.0) (amount / totalAmount) * 100 else 0.0
                val color = categoryColors.getOrPut(category) { generateRandomColor() }
                results.add(
                    ResultGetExpense(
                        category = "$category ($timeLabel)",
                        amount = amount,
                        percent = percent,
                        type = type,
                        total = totalAmount,
                        color = color
                    )
                )
            }
        }
        return results
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

    suspend fun getTransactionHistoryGroupedByDate(): List<GroupedTransaction> {
        val uid = getCurrentUserId() ?: return emptyList()
        return try {
            val snapshot = database.child(uid).get().await()
            val expenseItems = mutableListOf<ExpenseItemHistory>()
            for (child in snapshot.children) {
                // Lấy item từ snapshot và map trực tiếp vào ExpenseItemHistory
                val item = child.getValue(ExpenseItemHistory::class.java)
                val id = child.key ?: continue
                // Nếu item không null, gán id vào và thêm vào danh sách expenseItems
                item?.let {
                    val expenseItemWithId = it.copy(id = id) // Thêm id vào ExpenseItemHistory
                    expenseItems.add(expenseItemWithId)
                }
            }
            // Gom nhóm theo ngày và loại giao dịch (Thu nhập / Chi phí)
            val grouped = expenseItems.groupBy { Pair(it.date, it.type) }
            // Chuyển thành GroupedTransaction và sắp xếp theo ngày
            grouped.map { (dateAndType, items) ->
                val (date, type) = dateAndType
                GroupedTransaction(
                    date = date,
                    type = type,
                    total = items.sumOf { it.amount },
                    transactions = items.sortedByDescending { it.timestamp }
                )
            }.sortedByDescending { it.date } // Sắp xếp theo ngày mới nhất
        } catch (e: Exception) {
            emptyList() // Trả về danh sách rỗng nếu có lỗi
        }
    }

    suspend fun getExpensesByDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
        typeFilter: String? = null
    ): List<ExpenseItemHistory> {
        val uid = getCurrentUserId() ?: return emptyList()
        if (startDate == null || endDate == null) return emptyList()
        val oneYearAgo = LocalDate.now().minusYears(1)
        val startTimeMillisForQuery = startDate.minusDays(365).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endTimeMillisForQuery = endDate.plusDays(365).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        Log.d("ExpenseService", "Tìm kiếm theo ngày từ: $startDate đến: $endDate, loại: $typeFilter")

        return try {
            val snapshot = database.child(uid).get().await()
            val results = mutableListOf<ExpenseItemHistory>()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            for (child in snapshot.children) {
                val item = child.getValue(ExpenseItemHistory::class.java)
                val id = child.key ?: continue
                item?.let {
                    Log.d("ExpenseService", "Đang xử lý item: $it")
                    // Lọc các mục có timestamp nằm trong phạm vi rộng để đảm bảo không bỏ sót
                    if (it.timestamp in startTimeMillisForQuery until endTimeMillisForQuery) {
                        val matchesType = typeFilter.isNullOrBlank() || it.type.equals(typeFilter, ignoreCase = true)
                        Log.d("ExpenseService", "Matches type ($typeFilter): $matchesType, item type: ${it.type}")
                        if (matchesType) {
                            try {
                                val itemDate = LocalDate.parse(it.date, dateFormatter)
                                Log.d("ExpenseService", "Parsed date: $itemDate")
                                // Lọc chính xác theo trường date
                                if (!(startDate.isAfter(itemDate) || endDate.isBefore(itemDate))) {
                                    Log.d("ExpenseService", "Thêm item vào kết quả: $it")
                                    results.add(it.copy(id = id))
                                } else {
                                    Log.d("ExpenseService", "Loại bỏ do không nằm trong khoảng ngày: $itemDate")
                                }
                            } catch (e: Exception) {
                                Log.e("ExpenseService", "Lỗi parse ngày: ${e.message}")
                                // Xử lý lỗi parse nếu cần
                            }
                        } else {
                            Log.d("ExpenseService", "Loại bỏ do không khớp loại.")
                        }
                    } else {
                        Log.d("ExpenseService", "Loại bỏ do timestamp nằm ngoài phạm vi truy vấn.")
                    }
                }
            }
            Log.d("ExpenseService", "Số lượng kết quả trả về: ${results.size}")
            results.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            Log.e("ExpenseService", "Lỗi khi lấy chi phí theo khoảng ngày: ${e.message}")
            emptyList()
        }
    }
}