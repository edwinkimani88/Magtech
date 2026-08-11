package com.example.ui.sms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSmsScreen(
    viewModel: SmartSmsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text("AI Smart SMS Reminders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Meseji za Sheng na Kiswahili za Heshima", fontSize = 11.sp, color = TerracottaPeach)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            // Target Selector
            Text("1. Chagua Kundi la Wateja Target", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "DUE_TODAY" to "Due Today (${uiState.dueTodayLoans.size})",
                    "OVERDUE" to "Overdue (${uiState.overdueLoans.size})",
                    "ALL" to "All Customers"
                ).forEach { (key, label) ->
                    val isSel = uiState.filterTarget == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) TerracottaPeach else DarkSurface)
                            .clickable { viewModel.selectTargetFilter(key) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) TextOnTerracotta else Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Select Customer
            Text("2. Chagua Mteja", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.customers.isEmpty()) {
                Text("Hakuna customers waliopatikana.", fontSize = 12.sp, color = TextSecondary)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.customers) { customer ->
                        val isSelected = uiState.selectedCustomer?.id == customer.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) TerracottaDark else DarkSurface)
                                .clickable { viewModel.selectCustomerAndLoan(customer, null) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (isSelected) Color.White else TerracottaPeach)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(customer.fullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(customer.phoneNumber, fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Generated Message Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("3. Meseji iliyo-generated na AI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

                TextButton(onClick = viewModel::generateAiMessage) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TerracottaPeach, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Regenerate", fontSize = 12.sp, color = TerracottaPeach, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = uiState.generatedSmsText,
                onValueChange = viewModel::updateGeneratedText,
                label = { Text("SMS Body (Unaweza ku-edit hapa)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors = textFieldColors()
            )

            if (uiState.sendStatusMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = AccentGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp)) {
                    Text(uiState.sendStatusMessage!!, fontSize = 12.sp, color = AccentGreen, modifier = Modifier.padding(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = viewModel::sendSmsNow,
                enabled = uiState.selectedCustomer != null && uiState.generatedSmsText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = TextOnTerracotta)
                Spacer(modifier = Modifier.width(8.dp))
                Text("TUMA SMS LEA KWA SIMU", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent SMS Logs
            Text("Recent Sent SMS Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.smsLogs.isEmpty()) {
                Text("Hakuna SMS zilizo-loggwa bado.", fontSize = 12.sp, color = TextSecondary)
            } else {
                uiState.smsLogs.take(5).forEach { log ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("To: ${log.phoneNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TerracottaPeach)
                                Text(log.status, fontSize = 10.sp, color = AccentGreen)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.messageText, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
