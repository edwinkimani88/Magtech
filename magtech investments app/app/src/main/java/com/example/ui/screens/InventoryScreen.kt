package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
fun InventoryScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val shopFilter by viewModel.shopFilter.collectAsState()
    val allProducts by viewModel.products.collectAsState()

    val filteredProducts = remember(allProducts, shopFilter) {
        if (shopFilter == "all") allProducts else allProducts.filter { it.shopId == shopFilter }
    }

    var showAddProductDialog by remember { mutableStateOf(false) }
    var selectedProductForSale by remember { mutableStateOf<Product?>(null) }

    // Add Product Form State
    var prodName by remember { mutableStateOf("") }
    var prodPrice by remember { mutableStateOf("") }
    var prodCategory by remember { mutableStateOf("Phones") }
    var prodCondition by remember { mutableStateOf("GOOD") }
    var prodSource by remember { mutableStateOf("PURCHASE") }
    var prodDesc by remember { mutableStateOf("") }

    // Sale Form State
    var saleAmountText by remember { mutableStateOf("") }
    var buyerName by remember { mutableStateOf("") }
    var buyerPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "Inventory & Marketplace",
                currentShopFilter = shopFilter,
                onShopFilterChange = { viewModel.setShopFilter(it) },
                onHomeClick = onNavigateHome,
                onLogoutClick = {
                    viewModel.logout()
                    onLogout()
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = Color(0xFF00C8A8),
                contentColor = MagTechTealDark,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("ADD PRODUCT", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE0F2F1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = MagTechTealPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("SHARED MARKETPLACE CATALOGUE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagTechTealDark)
                        Text("Products created here appear automatically on the MagTech website.", fontSize = 11.sp, color = MagTechTealPrimary)
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (product.photoUrls.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.photoUrls.first()),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF3F4F6),
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Devices, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagTechTealDark)
                                Text("${product.category} • ${product.condition} • ${product.source}", fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("KSh ${String.format("%,.0f", product.price)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MagTechTealPrimary)
                            }

                            if (product.status == "AVAILABLE") {
                                Button(
                                    onClick = {
                                        selectedProductForSale = product
                                        saleAmountText = product.price.toInt().toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("SELL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEE2E2)
                                ) {
                                    Text("SOLD", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Add Product Dialog
        if (showAddProductDialog) {
            AlertDialog(
                onDismissRequest = { showAddProductDialog = false },
                title = { Text("Add Product to Inventory & Website", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = prodName,
                            onValueChange = { prodName = it },
                            label = { Text("Product Name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = prodPrice,
                            onValueChange = { prodPrice = it },
                            label = { Text("Price (KSh) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = prodDesc,
                            onValueChange = { prodDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val price = prodPrice.toDoubleOrNull() ?: 0.0
                            if (prodName.isNotBlank() && price > 0) {
                                viewModel.createProduct(
                                    name = prodName,
                                    category = prodCategory,
                                    condition = prodCondition,
                                    price = price,
                                    source = prodSource,
                                    description = prodDesc,
                                    photoUrls = emptyList(),
                                    isMarketplaceVisible = true
                                ) {
                                    showAddProductDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Text("SAVE PRODUCT")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddProductDialog = false }) { Text("CANCEL") }
                }
            )
        }

        // Sell Product Dialog
        selectedProductForSale?.let { prod ->
            AlertDialog(
                onDismissRequest = { selectedProductForSale = null },
                title = { Text("Record Sale for ${prod.name}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = saleAmountText,
                            onValueChange = { saleAmountText = it },
                            label = { Text("Sale Amount (KSh)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = buyerName,
                            onValueChange = { buyerName = it },
                            label = { Text("Buyer Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = buyerPhone,
                            onValueChange = { buyerPhone = it },
                            label = { Text("Buyer Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = saleAmountText.toDoubleOrNull() ?: prod.price
                            viewModel.recordSale(prod.id, amt, buyerName.ifBlank { "Walk-in Buyer" }, buyerPhone, "M-PESA") {
                                selectedProductForSale = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealPrimary)
                    ) {
                        Text("CONFIRM SALE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProductForSale = null }) { Text("CANCEL") }
                }
            )
        }
    }
}
