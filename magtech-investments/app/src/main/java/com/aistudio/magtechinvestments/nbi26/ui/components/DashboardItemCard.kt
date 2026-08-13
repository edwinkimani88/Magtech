package com.aistudio.magtechinvestments.nbi26.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.magtechinvestments.nbi26.data.db.entities.ItemEntity
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

@Composable
fun DashboardItemCard(
    item: ItemEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.category) {
                            "Phones" -> Icons.Default.Smartphone
                            "Laptops" -> Icons.Default.Laptop
                            "TVs & Audio" -> Icons.Default.Tv
                            "Gaming" -> Icons.Default.SportsEsports
                            else -> Icons.Default.Devices
                        },
                        contentDescription = item.category,
                        tint = TerracottaPeach
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.itemName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (item.shopLocation == "Shop 1") Color(0xFF1E3A8A) else Color(0xFF065F46))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (item.shopLocation == "Shop 1") "📍 Shop 1" else "📍 Shop 2",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${item.brand} • ${item.condition}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when (item.status) {
                        "LOANED" -> TerracottaPeach.copy(alpha = 0.2f)
                        "FORFEITED" -> AccentRed.copy(alpha = 0.2f)
                        "DIRECT_BUY" -> AccentGreen.copy(alpha = 0.2f)
                        else -> DarkSurfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.status) {
                            "LOANED" -> TerracottaPeach
                            "FORFEITED" -> AccentRed
                            "DIRECT_BUY" -> AccentGreen
                            else -> Color.White
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "KSh ${item.estimatedMarketValue.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

