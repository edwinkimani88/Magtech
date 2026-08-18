package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Loan
import com.example.data.models.LoanStatus
import com.example.ui.components.LoanStatusBadge
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.MagTechAccentGold
import com.example.ui.theme.MagTechStatusRed
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MagTechViewModel,
    onNavigateToCreateLoan: () -> Unit,
    onNavigateToLoanDetail: (String) -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToSmsAi: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val allLoans by viewModel.loans.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val allSales by viewModel.sales.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()

    // Filter data by selected shop
    val filteredLoans = remember(allLoans, shopFilter) {
        if (shopFilter == "all") allLoans else allLoans.filter { it.shopId == shopFilter }
    }

    val filteredSales = remember(allSales, shopFilter) {
        if (shopFilter == "all") allSales else allSales.filter { it.shopId == shopFilter }
    }

    val filteredTransactions = remember(allTransactions, shopFilter) {
        if (shopFilter == "all") allTransactions else allTransactions.filter { it.shopId == shopFilter }
    }

    // Daily Sales & Audits Calculation
    val todaySales = remember(filteredSales) {
        filteredSales // In practice, filtered by today's date
    }

    val todaySalesRevenue = remember(todaySales) {
        todaySales.sumOf { it.saleAmount }
    }

    val totalBalancePayable = remember(filteredLoans) {
        filteredLoans.filter { it.status == LoanStatus.ACTIVE || it.status == LoanStatus.PARTIALLY_PAID || it.status == LoanStatus.OVERDUE }
            .sumOf { it.balancePayable }
    }

    val overdueCount = remember(filteredLoans) {
        filteredLoans.count { it.status == LoanStatus.OVERDUE }
    }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Home & Daily Audits",
                currentShopFilter = shopFilter,
                onShopFilterChange = { viewModel.setShopFilter(it) },
                onHomeClick = {},
                onLogoutClick = {
                    viewModel.logout()
                    onLogout()
                },
                showHomeIcon = false
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateLoan,
                containerColor = Color(0xFF00C8A8),
                contentColor = MagTechTealDark,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.AddAPhoto, contentDescription = null) },
                text = { Text("NEW LOAN (PIGA PICHA)", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Daily Financial Overview Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Today's Sales Revenue",
                        value = "KSh ${String.format("%,.0f", todaySalesRevenue)}",
                        icon = Icons.Default.TrendingUp,
                        containerColor = Color(0xFFD1FAE5),
                        contentColor = Color(0xFF047857),
                        modifier = Modifier.weight(1f)
                    )

                    StatMetricCard(
                        title = "Today's Audit Events",
                        value = "${filteredTransactions.size} Logs",
                        icon = Icons.Default.ReceiptLong,
                        containerColor = MagTechTealDark,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMetricCard(
                        title = "Total Balance Payable",
                        value = "KSh ${String.format("%,.0f", totalBalancePayable)}",
                        icon = Icons.Default.AccountBalanceWallet,
                        containerColor = Color(0xFFF0FDFA),
                        contentColor = MagTechTealDark,
                        modifier = Modifier.weight(1.2f)
                    )

                    StatMetricCard(
                        title = "Overdue Loans",
                        value = "$overdueCount Active",
                        icon = Icons.Default.Warning,
                        containerColor = if (overdueCount > 0) Color(0xFFFEE2E2) else Color(0xFFECFDF5),
                        contentColor = if (overdueCount > 0) MagTechStatusRed else Color(0xFF047857),
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }

            // SECTION 1: TODAY'S SALES & DAILY AUDITS FEED
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = MagTechTealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TODAY'S SALES & AUDIT TRAIL",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MagTechTealPrimary
                        )
                    }

                    TextButton(onClick = onNavigateToTransactions) {
                        Text("View All Audits", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MagTechTealPrimary)
                    }
                }
            }

            if (filteredSales.isEmpty() && filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Hakuna sales au audit activities zimeresipitiwa leo kwa hii shop filter.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Display Today's Sales
                items(filteredSales.take(3)) { sale ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFD1FAE5),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.PointOfSale, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SALE: ${sale.buyerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagTechTealDark)
                                Text("Shop: ${if (sale.shopId == "shop_2") "Deliverance Rd" else "Chairman Rd"} • ${sale.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text("KSh ${String.format("%,.0f", sale.saleAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF047857))
                        }
                    }
                }

                // Display Recent Today's Audit Log
                items(filteredTransactions.take(3)) { tx ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when (tx.type) {
                                    "PAYMENT_RECEIVED" -> Color(0xFFD1FAE5)
                                    "RENEWAL_EXTENDED" -> Color(0xFFE0E7FF)
                                    else -> Color(0xFFFEF3C7)
                                },
                                modifier = Modifier.size(40.dp)
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
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagTechTealDark)
                                Text("Shop: ${if (tx.shopId == "shop_2") "Deliverance Rd" else "Chairman Rd"}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Text("KSh ${String.format("%,.0f", tx.amount)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagTechTealPrimary)
                        }
                    }
                }
            }

            // SECTION 2: ACTIVE LOAN REGISTERS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = MagTechTealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LOAN REGISTERS (${filteredLoans.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MagTechTealPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE0F2F1)
                    ) {
                        Text(
                            text = "Supabase Synced",
                            fontSize = 10.sp,
                            color = MagTechTealPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (filteredLoans.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Hakuna loans ziko active kwa hii shop sai.", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filteredLoans) { loan ->
                    LoanCardItem(
                        loan = loan,
                        onClick = { onNavigateToLoanDetail(loan.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MagTechTealPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagTechTealDark)
                Text(text = subtitle, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun LoanCardItem(
    loan: Loan,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = loan.customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MagTechTealDark
                    )
                    Text(
                        text = "${loan.loanNumber} • ${loan.customerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                LoanStatusBadge(status = loan.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            loan.collateralItems.firstOrNull()?.let { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Devices, contentDescription = null, tint = MagTechTealPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.category}: ${item.itemName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MagTechTealDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = "DUE DATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(text = loan.dueDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MagTechTealDark)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "BALANCE PAYABLE", fontSize = 10.sp, color = MagTechTealPrimary, fontWeight = FontWeight.Bold)
                    Text(
                        text = "KSh ${String.format("%,.0f", loan.balancePayable)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (loan.balancePayable <= 0) Color(0xFF047857) else MagTechTealDark
                    )
                }
            }
        }
    }
}
