package com.aistudio.magtechinvestments.nbi26.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.aistudio.magtechinvestments.nbi26.ui.navigation.Screen
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MagTechBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem(Screen.Dashboard.route, "Dashboard", Icons.Default.Home),
        NavItem(Screen.InventoryList.route, "Inventory", Icons.Default.Inventory2),
        NavItem(Screen.Marketplace.route, "Marketplace", Icons.Default.Storefront),
        NavItem(Screen.CustomerList.route, "Wateja", Icons.Default.People),
        NavItem(Screen.SmartSms.route, "SMS AI", Icons.Default.Sms)
    )

    NavigationBar(
        containerColor = DarkSurface,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) TerracottaPeach else TextSecondary
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) TerracottaPeach else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DarkSurfaceVariant
                )
            )
        }
    }
}

