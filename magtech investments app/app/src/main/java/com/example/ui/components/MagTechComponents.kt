package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LoanStatus
import com.example.data.models.Shop
import com.example.ui.theme.MagTechAccentGold
import com.example.ui.theme.MagTechStatusRed
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagTechTopAppBar(
    title: String,
    currentShopFilter: String,
    onShopFilterChange: (String) -> Unit,
    onHomeClick: () -> Unit,
    onLogoutClick: () -> Unit,
    showHomeIcon: Boolean = true
) {
    Surface(
        color = MagTechTealDark,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showHomeIcon) {
                        IconButton(
                            onClick = onHomeClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Return Home",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "MAGTECH INVESTMENTS • KITENGELA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00C8A8)
                        )
                    }
                }

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Shop Filter Pills (Shop 1, Shop 2, Combined)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShopFilterPill(
                    label = "Combined",
                    isSelected = currentShopFilter == "all",
                    onClick = { onShopFilterChange("all") },
                    modifier = Modifier.weight(1f)
                )
                ShopFilterPill(
                    label = "Chairman Rd",
                    isSelected = currentShopFilter == "shop_1",
                    onClick = { onShopFilterChange("shop_1") },
                    modifier = Modifier.weight(1f)
                )
                ShopFilterPill(
                    label = "Deliverance Rd",
                    isSelected = currentShopFilter == "shop_2",
                    onClick = { onShopFilterChange("shop_2") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ShopFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF00C8A8) else Color.White.copy(alpha = 0.15f),
        contentColor = if (isSelected) MagTechTealDark else Color.White,
        modifier = modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun LoanStatusBadge(status: LoanStatus) {
    val (bgColor, textColor, textLabel) = when (status) {
        LoanStatus.ACTIVE -> Triple(Color(0xFFE0F2F1), MagTechTealPrimary, "ACTIVE")
        LoanStatus.PARTIALLY_PAID -> Triple(Color(0xFFFEF3C7), MagTechAccentGold, "PARTIALLY PAID")
        LoanStatus.PAID -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), "PAID / CLEARED")
        LoanStatus.EXTENDED -> Triple(Color(0xFFE0E7FF), Color(0xFF4338CA), "EXTENDED")
        LoanStatus.OVERDUE -> Triple(Color(0xFFFEE2E2), MagTechStatusRed, "OVERDUE")
        LoanStatus.DEFAULTED -> Triple(Color(0xFF451A03), Color.White, "DEFAULTED")
        LoanStatus.CLOSED -> Triple(Color(0xFFF3F4F6), Color.Gray, "CLOSED")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = textLabel,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

data class NavigationBarItemData(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MagTechBottomNavigationBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit
) {
    val navItems = listOf(
        NavigationBarItemData("dashboard", "Loans", Icons.Default.AccountBalanceWallet),
        NavigationBarItemData("inventory", "Inventory", Icons.Default.Inventory2),
        NavigationBarItemData("marketplace", "Marketplace", Icons.Default.Storefront),
        NavigationBarItemData("transactions", "Audit", Icons.Default.ReceiptLong),
        NavigationBarItemData("sms_ai", "SMS AI", Icons.Default.Sms)
    )

    Surface(
        color = MagTechTealDark,
        shadowElevation = 8.dp,
        tonalElevation = 6.dp
    ) {
        NavigationBar(
            containerColor = MagTechTealDark,
            contentColor = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.navigationBarsPadding()
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route ||
                        (item.route == "dashboard" && currentRoute?.startsWith("loan_detail") == true) ||
                        (item.route == "dashboard" && currentRoute == "create_loan")

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            onNavigateToRoute(item.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MagTechTealDark,
                        selectedTextColor = Color(0xFF00C8A8),
                        indicatorColor = Color(0xFF00C8A8),
                        unselectedIconColor = Color.White.copy(alpha = 0.65f),
                        unselectedTextColor = Color.White.copy(alpha = 0.65f)
                    )
                )
            }
        }
    }
}
