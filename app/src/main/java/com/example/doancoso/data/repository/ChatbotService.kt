package com.example.doancoso.data.repository

import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.models.ResultGetExpense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ChatbotService {
    private val API_KEY = "AIzaSyB_W2RWFU88FEivEnBZeFHq8uM097CUySs"
    // Nếu bạn có quyền, hãy dùng model 1.5-flash, nếu không thì dùng 1.0-pro
    private val API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent"
    // Nếu lỗi 404 với 1.5-flash, hãy đổi lại thành:
    // private val API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.0-pro:generateContent"

    suspend fun getBotResponse(
        userMessage: String,
        expenseItems: List<ExpenseItem>,
        groupedExpenses: List<ResultGetExpense>
    ): String = withContext(Dispatchers.IO) {
        try {
            val context = buildContext(expenseItems, groupedExpenses)
            val prompt = """
                Context: $context

                User question: $userMessage

                Please provide a helpful response in Vietnamese based on the context and question.
                If the question is about financial data, use the provided data to give specific answers.
                If the question is not related to the context, provide a general helpful response.
            """.trimIndent()

            val partsArray = JSONArray().apply {
                put(JSONObject().apply { put("text", prompt) })
            }
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply { put("parts", partsArray) })
            }
            val requestBody = JSONObject().apply {
                put("contents", contentsArray)
            }

            val url = URL("$API_URL?key=$API_KEY")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                doInput = true
                useCaches = false
            }

            connection.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray())
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                throw Exception("API call failed with code $responseCode: $errorStream")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)

            val candidates = jsonResponse.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                if (parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).getString("text")
                }
            }

            return@withContext "Xin lỗi, tôi không thể xử lý câu hỏi của bạn lúc này."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Xin lỗi, đã có lỗi xảy ra khi xử lý câu hỏi của bạn. Chi tiết lỗi: ${e.message}"
        }
    }

    private fun buildContext(expenseItems: List<ExpenseItem>, groupedExpenses: List<ResultGetExpense>): String {
        val context = StringBuilder()

        // Thêm thông tin về các giao dịch gần đây
        context.append("Các giao dịch gần đây:\n")
        expenseItems.take(5).forEach { item ->
            context.append("- ${item.category}: ${item.amount} VNĐ (${item.type})\n")
            if (!item.note.isNullOrEmpty()) {
                context.append("  Ghi chú: ${item.note}\n")
            }
        }

        // Thêm thông tin về các nhóm chi tiêu
        context.append("\nTổng hợp chi tiêu theo danh mục:\n")
        groupedExpenses.forEach { expense ->
            context.append("- ${expense.category}: ${expense.amount} VNĐ\n")
        }

        return context.toString()
    }
}
