package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Loan
import com.example.data.models.LoanStatus
import com.example.ui.components.LoanStatusBadge
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsAiScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val shopFilter by viewModel.shopFilter.collectAsState()
    val loans by viewModel.loans.collectAsState()

    val overdueLoans = remember(loans, shopFilter) {
        val list = if (shopFilter == "all") loans else loans.filter { it.shopId == shopFilter }
        list.filter { it.status == LoanStatus.OVERDUE || it.status == LoanStatus.ACTIVE || it.status == LoanStatus.PARTIALLY_PAID }
    }

    var selectedLoan by remember { mutableStateOf<Loan?>(null) }
    var generatedSmsText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "AI SMS Hub (Nairobi Sheng)",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF92400E))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("NAIROBI SHENG SMS GENERATOR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF92400E))
                        Text("Uses GPT-4o-mini to compose polite yet firm Sheng debt collection SMS messages.", fontSize = 11.sp, color = Color(0xFFB45309))
                    }
                }
            }

            Text("SELECT BORROWER TO DRAFT SMS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagTechTealPrimary)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(overdueLoans) { loan ->
                    Card(
                        onClick = {
                            selectedLoan = loan
                            isGenerating = true
                            viewModel.generateSmsForLoan(loan) { text ->
                                generatedSmsText = text
                                isGenerating = false
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (loan.id == selectedLoan?.id) Color(0xFFE0F2F1) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(loan.customerName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagTechTealDark)
                                    Text("${loan.customerPhone} • Due: ${loan.dueDate}", fontSize = 11.sp, color = Color.Gray)
                                }
                                LoanStatusBadge(status = loan.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Balance Payable: KSh ${String.format("%,.0f", loan.balancePayable)}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MagTechTealPrimary)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }

        // Selected Loan SMS Draft Sheet / Dialog
        selectedLoan?.let { loan ->
            AlertDialog(
                onDismissRequest = { selectedLoan = null },
                title = { Text("Draft SMS for ${loan.customerName}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (isGenerating) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MagTechTealPrimary)
                            }
                            Text("Writing polite Sheng SMS via GPT-4o-mini...", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            OutlinedTextField(
                                value = generatedSmsText,
                                onValueChange = { generatedSmsText = it },
                                label = { Text("Message Body") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:${loan.customerPhone}")
                                putExtra("sms_body", generatedSmsText)
                            }
                            context.startActivity(intent)
                            selectedLoan = null
                        },
                        enabled = !isGenerating && generatedSmsText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OPEN SMS APP")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedLoan = null }) { Text("CANCEL") }
                }
            )
        }
    }
}
