package com.aistudio.magtechinvestments.nbi26.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.aistudio.magtechinvestments.nbi26.ui.components.DashboardItemCard
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    viewModel: CustomerProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToItemDetail: (Long) -> Unit,
    onNavigateToSmartSms: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val customer = uiState.customer

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = customer?.fullName ?: "Profile ya Mteja", fontSize = 16.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Rudi", tint = Color.White)
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
        } else if (customer == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Mteja haukupatikana", color = TextSecondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Header Profile Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(TerracottaPeach),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextOnTerracotta, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(customer.fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("National ID: ${customer.nationalId}", fontSize = 12.sp, color = TextSecondary)
                                Text("Simu: ${customer.phoneNumber}", fontSize = 12.sp, color = TerracottaPeach, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        val loans = uiState.loans
                        val totalBalance = loans.sumOf { it.totalPayable - it.paidAmount }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total Loans Disbursed", fontSize = 11.sp, color = TextSecondary)
                                Text("${loans.size} Loans", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column {
                                Text("Total Balance Payable", fontSize = 11.sp, color = TextSecondary)
                                Text("KSh ${totalBalance.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (totalBalance > 0) AccentRed else AccentGreen)
                            }
                        }

                        if (customer.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Notes: ${customer.notes}", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Items list for customer
                Text("Electronics za Mteja Huyu (Items Deposited / Sold)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.items.isEmpty()) {
                    Text("Hakuna items recorded kwa mteja huyu.", fontSize = 12.sp, color = TextSecondary)
                } else {
                    uiState.items.forEach { item ->
                        DashboardItemCard(
                            item = item,
                            onClick = { onNavigateToItemDetail(item.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
