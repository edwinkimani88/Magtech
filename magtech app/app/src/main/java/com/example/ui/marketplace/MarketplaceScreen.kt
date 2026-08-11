package com.example.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.db.entities.ItemEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToItemDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "MagTech Marketplace Catalog",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Unified Stock across Shop 1 & Shop 2",
                        fontSize = 11.sp,
                        color = TerracottaPeach
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Tafuta item, brand au location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TerracottaPeach) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Shop Filter Row (All MagTech | Shop 1 | Shop 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All" to "All MagTech Stock", "Shop 1" to "📍 Shop 1 (Westlands)", "Shop 2" to "📍 Shop 2 (CBD)").forEach { (shopKey, label) ->
                    val isSel = uiState.selectedShopFilter == shopKey
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setShopFilter(shopKey) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TerracottaPeach,
                            selectedLabelColor = TextOnTerracotta,
                            containerColor = DarkSurface,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category filter row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(listOf("All", "Phones", "Laptops", "TVs & Audio", "Gaming", "Appliances")) { _, cat ->
                    val isSel = uiState.selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TerracottaPeach,
                            selectedLabelColor = TextOnTerracotta,
                            containerColor = DarkSurface,
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Hakuna marketplace items kwa category au branch hii", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(uiState.items) { item ->
                        MarketplaceItemCard(
                            item = item,
                            onTogglePublish = { viewModel.toggleMarketplacePublish(item.id, item.isPublishedToMarketplace) },
                            onClick = { onNavigateToItemDetail(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceItemCard(
    item: ItemEntity,
    onTogglePublish: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.shopLocation == "Shop 1") Color(0xFF1E3A8A) else Color(0xFF065F46))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (item.shopLocation == "Shop 1") "📍 Shop 1 (Westlands)" else "📍 Shop 2 (CBD)",
                                    fontSize = 10.sp,
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

                Switch(
                    checked = item.isPublishedToMarketplace,
                    onCheckedChange = { onTogglePublish() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextOnTerracotta,
                        checkedTrackColor = TerracottaPeach
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = DarkBorder, thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bei ya Resale Marketplace", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "KSh ${if (item.marketplacePrice > 0) item.marketplacePrice.toInt() else item.estimatedMarketValue.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TerracottaPeach
                    )
                }

                Surface(
                    color = if (item.isPublishedToMarketplace) AccentGreen.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (item.isPublishedToMarketplace) "PUBLISHED (Live)" else "UNPUBLISHED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isPublishedToMarketplace) AccentGreen else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
