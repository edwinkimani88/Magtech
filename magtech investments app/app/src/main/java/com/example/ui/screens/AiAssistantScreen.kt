package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val chatMessages by viewModel.aiChatMessages.collectAsState()
    var userQueryText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Bazu AI Business Assistant",
                currentShopFilter = shopFilter,
                onShopFilterChange = { viewModel.setShopFilter(it) },
                onHomeClick = onNavigateHome,
                onLogoutClick = {
                    viewModel.logout()
                    onLogout()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Info Banner
            Surface(
                color = MagTechTealDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF00C8A8))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("BAZU AI • LIVE BUSINESS INSIGHTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        Text("Powered by OpenRouter GPT-4o-mini with real-time shop stats.", fontSize = 10.sp, color = Color(0xFF00C8A8))
                    }
                }
            }

            // Chat History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }

                items(chatMessages) { (text, isUser) ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            color = if (isUser) MagTechTealPrimary else Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = text,
                                modifier = Modifier.padding(14.dp),
                                fontSize = 13.sp,
                                color = if (isUser) Color.White else MagTechTealDark,
                                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            // Input Bar
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userQueryText,
                        onValueChange = { userQueryText = it },
                        placeholder = { Text("Ask Bazu AI e.g. Who owes us today?", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (userQueryText.isNotBlank()) {
                                val query = userQueryText
                                userQueryText = ""
                                viewModel.sendAiAssistantQuery(query)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MagTechTealPrimary, shape = RoundedCornerShape(24.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
