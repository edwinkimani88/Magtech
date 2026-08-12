package com.example.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import coil.compose.AsyncImage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    viewModel: ItemDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCustomerProfile: (Long) -> Unit,
    onNavigateToSmartSms: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var paymentAmountInput by remember { mutableStateOf("") }
    var showPaymentDialog by remember { mutableStateOf(false) }

    val item = uiState.item
    val loan = uiState.loan
    val customer = uiState.customer

    var showExtendDialog by remember { mutableStateOf(false) }
    var renewalFeeInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = item?.itemName ?: "Taarifa za Item", fontSize = 16.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSmartSms) {
                        Icon(Icons.Default.Sms, contentDescription = "SMS", tint = TerracottaPeach)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TerracottaPeach)
            }
        } else if (item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Item haikupatikana", color = TextSecondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Photos viewer
                val photos = item.photoUrlsJson.split(",").filter { it.isNotBlank() }
                if (photos.isNotEmpty()) {
                    Text("Picha za Item Permanently Attached:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(photos) { ph ->
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, TerracottaPeach, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (ph.startsWith("http") || ph.startsWith("content") || ph.startsWith("file")) {
                                    AsyncImage(
                                        model = ph,
                                        contentDescription = "Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = TerracottaPeach, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Verified Photo", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (item.shopLocation == "Shop 1") Color(0xFF1E3A8A) else Color(0xFF065F46))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("📍 ${item.shopLocation}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Text("${item.brand} • ${item.category}", fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            Surface(
                                color = TerracottaPeach.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(item.status, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TerracottaPeach, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }

                        if (item.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Notes: ${item.notes}", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Customer section if applicable
                if (customer != null) {
                    Text("Mteja Aliye-Deposit Item", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkSurface)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(customer.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("ID: ${customer.nationalId} • Simu: ${customer.phoneNumber}", fontSize = 12.sp, color = TextSecondary)
                            }
                            Button(
                                onClick = { onNavigateToCustomerProfile(customer.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Profile >", fontSize = 12.sp, color = TextOnTerracotta)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Loan Section & Repayment
                if (loan != null) {
                    Text("Loan & Repayment Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    val balance = loan.totalPayable - loan.paidAmount

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurface)
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Amount Given", fontSize = 11.sp, color = TextSecondary)
                                    Text("KSh ${loan.amountGiven.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column {
                                    Text("Total Payable", fontSize = 11.sp, color = TextSecondary)
                                    Text("KSh ${loan.totalPayable.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column {
                                    Text("Balance Payable", fontSize = 11.sp, color = TextSecondary)
                                    Text("KSh ${balance.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (balance > 0) AccentRed else AccentGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (balance > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showPaymentDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Payment, contentDescription = null, tint = TextOnTerracotta)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lipa Balance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
                                    }

                                    Button(
                                        onClick = { showExtendDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TextOnTerracotta)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Extend / Renew", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Marketplace Publish Switch
                Text("Marketplace Integration", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Publish on Shared Marketplace", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Itaonyesha badget ya ${item.shopLocation}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = item.isPublishedToMarketplace,
                            onCheckedChange = viewModel::toggleMarketplace,
                            colors = SwitchDefaults.colors(checkedThumbColor = TextOnTerracotta, checkedTrackColor = TerracottaPeach)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Repayment Dialog
        if (showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { showPaymentDialog = false },
                title = { Text("Rekodi Malipo ya Loan", color = Color.White) },
                text = {
                    Column {
                        Text("Ingiza kiasi mteja amelipa (KSh):", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = paymentAmountInput,
                            onValueChange = { paymentAmountInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = textFieldColors()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = paymentAmountInput.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.recordRepayment(amt)
                                showPaymentDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                    ) {
                        Text("Lipa", color = TextOnTerracotta)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaymentDialog = false }) {
                        Text("Ghairi", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        }

        // Loan Extension / Renewal Dialog
        if (showExtendDialog) {
            AlertDialog(
                onDismissRequest = { showExtendDialog = false },
                title = { Text("Extend / Renew Loan Due Date", color = Color.White) },
                text = {
                    Column {
                        Text("Ingiza Ada ya Renewal/Extension (KSh):", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = renewalFeeInput,
                            onValueChange = { renewalFeeInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = textFieldColors()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Hii itaongeza tarehe ya due date kwa siku 14 na marekebisho yatarekodiwa Supabase.", fontSize = 11.sp, color = TerracottaPeach)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val fee = renewalFeeInput.toDoubleOrNull() ?: 0.0
                            if (fee >= 0) {
                                viewModel.extendLoan(fee, extensionDays = 14)
                                showExtendDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach)
                    ) {
                        Text("Extend Siku 14", color = TextOnTerracotta)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExtendDialog = false }) {
                        Text("Ghairi", color = TextSecondary)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}
