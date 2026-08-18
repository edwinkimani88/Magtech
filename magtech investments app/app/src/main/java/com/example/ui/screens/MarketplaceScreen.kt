package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.models.Product
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val allProducts by viewModel.products.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Phones", "Laptops", "TVs", "Audio", "Fridges", "Appliances")

    val marketplaceProducts = remember(allProducts, shopFilter, searchQuery, selectedCategory) {
        allProducts.filter { product ->
            val matchShop = shopFilter == "all" || product.shopId == shopFilter
            val matchCategory = selectedCategory == "ALL" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true) || product.description.contains(searchQuery, ignoreCase = true)
            matchShop && matchCategory && matchQuery && product.status == "AVAILABLE"
        }
    }

    var selectedProductForSale by remember { mutableStateOf<Product?>(null) }
    var saleAmountText by remember { mutableStateOf("") }
    var buyerName by remember { mutableStateOf("") }
    var buyerPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Kitengela Marketplace",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Marketplace Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MagTechTealDark,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00C8A8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF00C8A8))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "MAGTECH ONLINE & IN-STORE CATALOGUE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00C8A8)
                        )
                        Text(
                            text = "Publicly listed items across Kitengela branches",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search electronics, phones, TVs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MagTechTealPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MagTechTealPrimary,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(4).forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (marketplaceProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No marketplace items matching filter.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(marketplaceProducts) { product ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (product.photoUrls.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(product.photoUrls.first()),
                                            contentDescription = product.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(Icons.Default.Devices, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                    }

                                    // Branch Badge
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MagTechTealDark.copy(alpha = 0.85f),
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(6.dp)
                                    ) {
                                        Text(
                                            text = if (product.shopId == "shop_2") "Deliverance" else "Chairman Rd",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MagTechTealDark,
                                    maxLines = 1
                                )

                                Text(
                                    text = "${product.category} • ${product.condition}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "KSh ${String.format("%,.0f", product.price)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = MagTechTealPrimary
                                    )

                                    IconButton(
                                        onClick = {
                                            selectedProductForSale = product
                                            saleAmountText = product.price.toInt().toString()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCartCheckout,
                                            contentDescription = "Buy / Sell",
                                            tint = MagTechTealPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sell / Checkout Dialog
        selectedProductForSale?.let { prod ->
            AlertDialog(
                onDismissRequest = { selectedProductForSale = null },
                title = { Text("Checkout / Sell ${prod.name}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Branch Location: ${if (prod.shopId == "shop_2") "Deliverance Rd Branch" else "Chairman Rd Branch"}", fontSize = 12.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = saleAmountText,
                            onValueChange = { saleAmountText = it },
                            label = { Text("Selling Price (KSh)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = buyerName,
                            onValueChange = { buyerName = it },
                            label = { Text("Customer / Buyer Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = buyerPhone,
                            onValueChange = { buyerPhone = it },
                            label = { Text("Customer Phone Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = saleAmountText.toDoubleOrNull() ?: prod.price
                            viewModel.recordSale(
                                productId = prod.id,
                                saleAmount = amt,
                                buyerName = buyerName.ifBlank { "Marketplace Buyer" },
                                buyerPhone = buyerPhone,
                                method = "M-PESA"
                            ) {
                                selectedProductForSale = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Text("RECORD MARKETPLACE SALE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProductForSale = null }) { Text("CANCEL") }
                }
            )
        }
    }
}
