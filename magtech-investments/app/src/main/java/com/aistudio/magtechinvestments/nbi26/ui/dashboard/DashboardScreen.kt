package com.aistudio.magtechinvestments.nbi26.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    userRole: String,
    userName: String,
    onNavigateToNewLoan: () -> Unit,
    onNavigateToDirectPurchase: () -> Unit,
    onNavigateToMarketplace: () -> Unit,
    onNavigateToSmartSms: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToItemDetail: (Long) -> Unit,
    onNavigateToReports: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sasa, ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(userName, fontSize = 16.sp, fontWeight = FontWeight.Black, color = TerracottaPeach)
                        }
                        Text("MagTech Investments • Nairobi System", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAiAssistant) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = TerracottaPeach
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 10.dp)
        ) {
            // 1. Shop Filter Bar (All Shops | Shop 1 | Shop 2)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("All Shops", "Shop 1", "Shop 2").forEach { shop ->
                        val isSelected = uiState.selectedShopFilter == shop
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TerracottaPeach else Color.Transparent)
                                .clickable { viewModel.selectShopFilter(shop) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (shop == "All Shops") "MagTech Total" else shop,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextOnTerracotta else TextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Quick Action Launcher Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Niaje Boss!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Chukua loan mpya au nunua item haraka", fontSize = 12.sp, color = TextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TerracottaPeach)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = uiState.selectedShopFilter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnTerracotta
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onNavigateToNewLoan,
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TextOnTerracotta)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LOAN MPYA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
                            }

                            Button(
                                onClick = onNavigateToDirectPurchase,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NUNUA ITEM", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 3. Loans Due Today Alert Banner
            if (uiState.loansDueTodayCount > 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(TerracottaPeach)
                            .clickable { onNavigateToSmartSms() }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = TextOnTerracotta)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Boss, ${uiState.loansDueTodayCount} Loans Ziko Due Leo!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextOnTerracotta
                                    )
                                    Text(
                                        text = "Tuma Smart SMS reminders kwa wateja sai",
                                        fontSize = 11.sp,
                                        color = Color(0xFF3C2010)
                                    )
                                }
                            }
                            Icon(Icons.Default.Send, contentDescription = null, tint = TextOnTerracotta)
                        }
                    }
                }
            }

            // 4. Financial Metrics Overview with Time Period Filter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Summary za Pesa", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            com.aistudio.magtechinvestments.nbi26.util.TimePeriod.values().forEach { period ->
                                val isSelected = uiState.selectedTimePeriod == period
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TerracottaPeach else DarkSurface)
                                        .clickable { viewModel.selectTimePeriod(period) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = period.label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextOnTerracotta else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Revenue (${uiState.selectedTimePeriod.label})",
                            value = "KSh ${uiState.totalRevenueKsh.toInt()}",
                            icon = Icons.Default.TrendingUp,
                            accentColor = AccentGreen,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReports
                        )

                        MetricCard(
                            title = "Disbursed (${uiState.selectedTimePeriod.label})",
                            value = "KSh ${uiState.totalDisbursedKsh.toInt()}",
                            icon = Icons.Default.AccountBalanceWallet,
                            accentColor = TerracottaPeach,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReports
                        )
                    }
                }
            }

            // 5. Inventory & Loans Stat Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("Inventory", "${uiState.totalInventoryCount} items", Icons.Default.Inventory, Modifier.weight(1f))
                    StatCard("Active Loans", "${uiState.activeLoansCount}", Icons.Default.Handshake, Modifier.weight(1f))
                    StatCard("Marketplace", "${uiState.marketplaceCount}", Icons.Default.Storefront, Modifier.weight(1f)) {
                        onNavigateToMarketplace()
                    }
                }
            }

            // 6. Quick Nav Shortcuts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ShortcutChip("Smart SMS", Icons.Default.Sms, onNavigateToSmartSms)
                    ShortcutChip("AI Valuation", Icons.Default.AutoAwesome, onNavigateToAiAssistant)
                    ShortcutChip("Audit Reports", Icons.Default.Assessment, onNavigateToReports)
                }
            }

            // 7. Recent Items Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Items Mpya Hapa MagTech", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = "Check items zote",
                        fontSize = 12.sp,
                        color = TerracottaPeach,
                        modifier = Modifier.clickable { onNavigateToMarketplace() }
                    )
                }
            }

            if (uiState.recentItems.isEmpty()) {
                item {
                    Text("Hakuna item grounded au iliyo ingizwa hapa karibuni.", fontSize = 12.sp, color = TextSecondary)
                }
            } else {
                items(uiState.recentItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToItemDetail(item.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                                    Text(item.category, fontSize = 11.sp, color = TextSecondary)
                                    Text("•", fontSize = 11.sp, color = TextSecondary)
                                    Text(item.status, fontSize = 11.sp, color = TerracottaPeach, fontWeight = FontWeight.Medium)
                                }
                            }
                            Text(
                                text = "KSh ${item.estimatedMarketValue.toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(12.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = TerracottaPeach, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun ShortcutChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TerracottaPeach, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

