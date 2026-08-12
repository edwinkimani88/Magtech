package com.aistudio.magtechinvestments.nbi26.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTransaction by remember { mutableStateOf<com.aistudio.magtechinvestments.nbi26.data.db.entities.TransactionEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ripoti za Biashara (Financial Audit)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("MagTech Investments Audit Logs", fontSize = 11.sp, color = TerracottaPeach)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Rudi", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // Shop Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("All Shops", "Shop 1", "Shop 2").forEach { shop ->
                    val isSel = uiState.selectedShopFilter == shop
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) TerracottaPeach else Color.Transparent)
                            .clickable { viewModel.selectShopFilter(shop) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (shop == "All Shops") "MagTech Total" else shop,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) TextOnTerracotta else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time Period Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                com.aistudio.magtechinvestments.nbi26.util.TimePeriod.values().forEach { period ->
                    val isSel = uiState.selectedTimePeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) TerracottaPeach else Color.Transparent)
                            .clickable { viewModel.selectTimePeriod(period) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) TextOnTerracotta else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Summary Card Grid
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
                        Text("Financial Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TerracottaPeach)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(uiState.selectedShopFilter, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Total Revenue Collected", fontSize = 11.sp, color = TextSecondary)
                            Text("KSh ${uiState.totalRevenue.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AccentGreen)
                        }
                        Column {
                            Text("Total Loans Disbursed", fontSize = 11.sp, color = TextSecondary)
                            Text("KSh ${uiState.totalLoansDisbursed.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TerracottaPeach)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Direct Stock Purchases", fontSize = 11.sp, color = TextSecondary)
                            Text("KSh ${uiState.totalDirectPurchases.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700))
                        }
                        Column {
                            Text("Audit Transactions", fontSize = 11.sp, color = TextSecondary)
                            Text("${uiState.totalTransactionsCount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Transaction Audit Feed", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.transactions.isEmpty()) {
                Text("Hakuna transactions zilizopatikana kwa branch hii.", fontSize = 12.sp, color = TextSecondary)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.transactions) { tx ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface)
                                .clickable { selectedTransaction = tx }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tx.description, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (tx.shopLocation == "Shop 1") Color(0xFF1E3A8A) else Color(0xFF065F46))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(tx.shopLocation, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Text("Type: ${tx.type}", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                                Text(
                                    text = "KSh ${tx.amount.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (tx.type == "LOAN_REPAYMENT" || tx.type == "MARKETPLACE_SALE") AccentGreen else TerracottaPeach
                                )
                            }
                        }
                    }
                }
            }
        }

        // Transaction Detail Dialog
        if (selectedTransaction != null) {
            val tx = selectedTransaction!!
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.US).format(java.util.Date(tx.timestamp))
            AlertDialog(
                onDismissRequest = { selectedTransaction = null },
                title = { Text("Taarifa za Transaction Audit", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Transaction ID: #${tx.id}", fontSize = 12.sp, color = TerracottaPeach)
                        Text("Description: ${tx.description}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Divider(color = DarkBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Aina (Type):", fontSize = 12.sp, color = TextSecondary)
                            Text(tx.type, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kiasi (Amount):", fontSize = 12.sp, color = TextSecondary)
                            Text("KSh ${tx.amount.toInt()}", fontSize = 14.sp, color = AccentGreen, fontWeight = FontWeight.Black)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Branch Shop:", fontSize = 12.sp, color = TextSecondary)
                            Text(tx.shopLocation, fontSize = 12.sp, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Date & Time:", fontSize = 12.sp, color = TextSecondary)
                            Text(dateStr, fontSize = 12.sp, color = Color.White)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedTransaction = null },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach)
                    ) {
                        Text("Funga", color = TextOnTerracotta)
                    }
                },
                containerColor = DarkSurface
            )
        }
    }
}
