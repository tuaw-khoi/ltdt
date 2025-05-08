package com.example.doancoso.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doancoso.data.models.ExpenseItem
import com.example.doancoso.data.models.ResultGetExpense
import com.example.doancoso.data.repository.AuthService
import com.example.doancoso.data.repository.ChatbotService
import com.example.doancoso.data.repository.ExpenseItemService
import kotlinx.coroutines.launch

data class ChatMessage(
    val message: String,
    val isFromUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    navController: NavHostController,
    authService: AuthService,
    expenseItemService: ExpenseItemService = remember { ExpenseItemService() }
) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }
    val chatbotService = remember { ChatbotService() }
    val coroutineScope = rememberCoroutineScope()
    
    // Load initial data
    var expenseItems by remember { mutableStateOf(listOf<ExpenseItem>()) }
    var groupedExpenses by remember { mutableStateOf(listOf<ResultGetExpense>()) }
    
    LaunchedEffect(Unit) {
        expenseItems = expenseItemService.getAllExpenses()
        groupedExpenses = expenseItemService.getGroupedExpensesBy("Chi phí", "month")
        
        // Add welcome message
        messages = listOf(
            ChatMessage(
                "Xin chào! Tôi là trợ lý tài chính của bạn. Tôi có thể giúp bạn:\n" +
                "1. Xem thông tin về các giao dịch gần đây\n" +
                "2. Phân tích chi tiêu theo danh mục\n" +
                "3. Tư vấn về quản lý tài chính\n" +
                "Bạn có thể hỏi tôi bất kỳ câu hỏi nào liên quan đến tài chính của bạn.",
                false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chatbot Hỗ Trợ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Chat messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    ChatMessageItem(message)
                }
            }

            // Message input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    placeholder = { Text("Nhập tin nhắn...") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isLoading) {
                            val userMessage = messageText
                            messages = messages + ChatMessage(userMessage, true)
                            messageText = ""
                            isLoading = true
                            
                            coroutineScope.launch {
                                try {
                                    val response = chatbotService.getBotResponse(
                                        userMessage,
                                        expenseItems,
                                        groupedExpenses
                                    )
                                    messages = messages + ChatMessage(response, false)
                                } catch (e: Exception) {
                                    messages = messages + ChatMessage(
                                        "Xin lỗi, đã có lỗi xảy ra khi xử lý câu hỏi của bạn.",
                                        false
                                    )
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF00796B)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color(0xFF00796B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (message.isFromUser) Color(0xFF00796B) else Color.White
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                color = if (message.isFromUser) Color.White else Color.Black,
                fontSize = 16.sp
            )
        }
    }
} 