package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.components.LoanStatusBadge
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailScreen(
    loanId: String,
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToSmsAi: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val loan = loans.find { it.id == loanId }

    var showPaymentDialog by remember { mutableStateOf(false) }
    var paymentAmountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("M-PESA") }

    var showRenewalDialog by remember { mutableStateOf(false) }
    var renewalFeeText by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 14) }
    var newDueDateText by remember { mutableStateOf(dateFormat.format(cal.time)) }

    var generatedAiSmsMessage by remember { mutableStateOf<String?>(null) }
    var isGeneratingSms by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Loan ${loan?.loanNumber ?: "Details"}",
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
        if (loan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loan record not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Balance Payable Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MagTechTealDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BALANCE PAYABLE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00C8A8),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "KSh ${String.format("%,.0f", loan.balancePayable)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        LoanStatusBadge(status = loan.status)

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Handed Out", fontSize = 10.sp, color = Color.White.copy(0.7f))
                                Text("KSh ${String.format("%,.0f", loan.loanAmount)}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Payable", fontSize = 10.sp, color = Color.White.copy(0.7f))
                                Text("KSh ${String.format("%,.0f", loan.amountPayable)}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Paid", fontSize = 10.sp, color = Color.White.copy(0.7f))
                                Text("KSh ${String.format("%,.0f", loan.totalPaid)}", fontWeight = FontWeight.Bold, color = Color(0xFF00C8A8), fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Customer Details Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("CUSTOMER INFORMATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagTechTealPrimary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MagTechTealPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(loan.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(loan.customerPhone, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ID Number: ${loan.customerIdNumber.ifBlank { "N/A" }}", fontSize = 13.sp)
                        }
                    }
                }

                // Collateral Items Section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("COLLATERAL ITEM HELD", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagTechTealPrimary)

                        loan.collateralItems.forEach { item ->
                            Text(item.itemName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Category: ${item.category} • Condition: ${item.condition}", fontSize = 12.sp, color = Color.Gray)
                            if (item.description.isNotBlank()) {
                                Text("Notes: ${item.description}", fontSize = 12.sp, color = Color.DarkGray)
                            }

                            if (item.photoUrls.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(item.photoUrls) { url ->
                                        Image(
                                            painter = rememberAsyncImagePainter(url),
                                            contentDescription = "Collateral Photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons (Record Payment, Extend Loan, SMS AI)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showPaymentDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PAYMENT", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showRenewalDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Update, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXTEND LOAN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // AI SMS Generator Action (PART 31 & PART 32)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, tint = MagTechTealPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AI SMS Generator (Sheng)", fontWeight = FontWeight.Bold, color = MagTechTealDark)
                            }

                            Button(
                                onClick = {
                                    isGeneratingSms = true
                                    viewModel.generateSmsForLoan(loan) { text ->
                                        generatedAiSmsMessage = text
                                        isGeneratingSms = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C8A8), contentColor = MagTechTealDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isGeneratingSms) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MagTechTealDark)
                                } else {
                                    Text("GENERATE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        generatedAiSmsMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = msg, fontSize = 13.sp, color = MagTechTealDark)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = onNavigateToSmsAi) {
                                            Text("Open SMS Hub", fontWeight = FontWeight.Bold, color = MagTechTealPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Record Payment Dialog
        if (showPaymentDialog && loan != null) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("Record Loan Payment", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Current Balance: KSh ${String.format("%,.0f", loan.balancePayable)}", fontSize = 13.sp)

                        OutlinedTextField(
                            value = paymentAmountText,
                            onValueChange = { paymentAmountText = it },
                            label = { Text("Payment Amount (KSh)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Payment Method", fontSize = 12.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("M-PESA", "CASH", "BANK").forEach { method ->
                                FilterChip(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    label = { Text(method) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = paymentAmountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.recordPayment(loan.id, amt, paymentMethod) {
                                    showPaymentDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Text("CONFIRM PAYMENT")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaymentDialog = false }) { Text("CANCEL") }
                }
            )
        }

        // Extend / Renew Loan Dialog (PART 21)
        if (showRenewalDialog && loan != null) {
            AlertDialog(
                onDismissRequest = { showRenewalDialog = false },
                title = { Text("Extend Loan Due Date", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Existing Due Date: ${loan.dueDate}", fontSize = 13.sp)

                        OutlinedTextField(
                            value = renewalFeeText,
                            onValueChange = { renewalFeeText = it },
                            label = { Text("Renewal Fee Paid (KSh)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newDueDateText,
                            onValueChange = { newDueDateText = it },
                            label = { Text("New Due Date (YYYY-MM-DD)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val fee = renewalFeeText.toDoubleOrNull() ?: 0.0
                            viewModel.extendLoan(loan.id, fee, newDueDateText) {
                                showRenewalDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Text("CONFIRM EXTENSION")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenewalDialog = false }) { Text("CANCEL") }
                }
            )
        }
    }
}
