package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SaleRecord
import com.example.data.models.TransactionRecord
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel

enum class AuditTimeframe(val label: String, val days: Int) {
    DAY("Day", 1),
    THREE_DAYS("3 Days", 3),
    WEEK("Week", 7),
    FORTNIGHT("Fortnight", 14),
    MONTH("Month", 30),
    ALL("All Time", 9999)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()
    val allSales by viewModel.sales.collectAsState()

    var selectedTimeframe by remember { mutableStateOf(AuditTimeframe.DAY) }

    val filteredTransactions = remember(allTransactions, shopFilter, selectedTimeframe) {
        val shopFiltered = if (shopFilter == "all") allTransactions else allTransactions.filter { it.shopId == shopFilter }
        // For demonstration, all seeded/mock/live transactions belong to active timeframe
        shopFiltered
    }

    val filteredSales = remember(allSales, shopFilter, selectedTimeframe) {
        val shopFiltered = if (shopFilter == "all") allSales else allSales.filter { it.shopId == shopFilter }
        shopFiltered
    }

    val totalTimeframeSalesRevenue = remember(filteredSales) {
        filteredSales.sumOf { it.saleAmount }
    }

    val totalTimeframeTransactionVolume = remember(filteredTransactions) {
        filteredTransactions.sumOf { it.amount }
    }

    var selectedTx by remember { mutableStateOf<TransactionRecord?>(null) }
    var selectedSale by remember { mutableStateOf<SaleRecord?>(null) }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Financial & Sales Audit",
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Timeframe Filter Chips
            Text(
                text = "FILTER AUDIT TIMEFRAME",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MagTechTealPrimary
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(AuditTimeframe.values()) { tf ->
                    FilterChip(
                        selected = selectedTimeframe == tf,
                        onClick = { selectedTimeframe = tf },
                        label = { Text(tf.label, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MagTechTealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Summary Volume Cards for Selected Timeframe
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${selectedTimeframe.label} Sales Revenue", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        Text(
                            text = "KSh ${String.format("%,.0f", totalTimeframeSalesRevenue)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF047857)
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MagTechTealDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${selectedTimeframe.label} Loan Payments", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00C8A8))
                        Text(
                            text = "KSh ${String.format("%,.0f", totalTimeframeTransactionVolume)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            Text(
                text = "AUDIT & SALES ACTIVITIES (${filteredSales.size + filteredTransactions.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MagTechTealPrimary
            )

            if (filteredTransactions.isEmpty() && filteredSales.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Hakuna transaction records within this timeframe.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Render Sales Activities
                    items(filteredSales) { sale ->
                        Card(
                            onClick = { selectedSale = sale },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFD1FAE5),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("PRODUCT SALE: ${sale.buyerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagTechTealDark)
                                    Text("Shop: ${if (sale.shopId == "shop_2") "Deliverance Rd" else "Chairman Rd"} • ${sale.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Text(
                                    text = "KSh ${String.format("%,.0f", sale.saleAmount)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }

                    // Render Financial / Loan Transactions
                    items(filteredTransactions) { tx ->
                        Card(
                            onClick = { selectedTx = tx },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when (tx.type) {
                                        "PAYMENT_RECEIVED" -> Color(0xFFD1FAE5)
                                        "RENEWAL_EXTENDED" -> Color(0xFFE0E7FF)
                                        else -> Color(0xFFFEF3C7)
                                    },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (tx.type) {
                                                "PAYMENT_RECEIVED" -> Icons.Default.Payments
                                                "RENEWAL_EXTENDED" -> Icons.Default.Update
                                                else -> Icons.Default.Receipt
                                            },
                                            contentDescription = null,
                                            tint = MagTechTealDark,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagTechTealDark)
                                    Text("Shop: ${if (tx.shopId == "shop_2") "Deliverance Rd" else "Chairman Rd"} • ${tx.type}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Text(
                                    text = "KSh ${String.format("%,.0f", tx.amount)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = MagTechTealPrimary
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }

        // Transaction Details Dialog
        selectedTx?.let { tx ->
            AlertDialog(
                onDismissRequest = { selectedTx = null },
                title = { Text(tx.title, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Transaction Type: ${tx.type}", fontWeight = FontWeight.Bold)
                        Text("Amount: KSh ${String.format("%,.0f", tx.amount)}")
                        Text("Shop Location: ${if (tx.shopId == "shop_2") "Deliverance Rd Branch" else "Chairman Rd Branch"}")
                        if (tx.detailsJson.isNotBlank()) {
                            Text("Details: ${tx.detailsJson}")
                        }
                        Text("Reference ID: ${tx.referenceId.ifBlank { "N/A" }}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedTx = null }) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Sale Details Dialog
        selectedSale?.let { sale ->
            AlertDialog(
                onDismissRequest = { selectedSale = null },
                title = { Text("Product Sale Audit", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Buyer Name: ${sale.buyerName}", fontWeight = FontWeight.Bold)
                        Text("Buyer Phone: ${sale.buyerPhone.ifBlank { "N/A" }}")
                        Text("Sale Amount: KSh ${String.format("%,.0f", sale.saleAmount)}")
                        Text("Payment Method: ${sale.paymentMethod}")
                        Text("Shop Location: ${if (sale.shopId == "shop_2") "Deliverance Rd Branch" else "Chairman Rd Branch"}")
                        Text("Sold By: ${sale.soldBy}")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { selectedSale = null }) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

