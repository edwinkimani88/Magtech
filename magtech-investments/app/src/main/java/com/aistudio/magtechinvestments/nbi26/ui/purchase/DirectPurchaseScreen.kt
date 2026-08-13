package com.aistudio.magtechinvestments.nbi26.ui.purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.magtechinvestments.nbi26.ui.components.PhotoPickerComponent
import com.aistudio.magtechinvestments.nbi26.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectPurchaseScreen(
    viewModel: DirectPurchaseViewModel,
    onNavigateBack: () -> Unit,
    onItemPurchased: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.successItemId) {
        uiState.successItemId?.let { id ->
            onItemPurchased(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nunua Item Direct (Outright Buy)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Nunua mkononi kwa mteja au supplier", fontSize = 11.sp, color = TerracottaPeach)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Rudi", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // 0. Shop Location Selector
            Text(
                text = "Duka Linalo-buy Item Hii (Shop Branch)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Shop 1", "Shop 2").forEach { shop ->
                    val isSel = uiState.shopLocation == shop
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) TerracottaPeach else Color.Transparent)
                            .clickable { viewModel.updateShopLocation(shop) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (shop == "Shop 1") "Shop 1 (Chairman Rd)" else "Shop 2 (Deliverance Rd)",
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) TextOnTerracotta else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. Camera Photo Picker Component
            PhotoPickerComponent(
                photos = uiState.photoUrls,
                onPhotosUpdated = viewModel::updatePhotos,
                minPhotos = 2,
                maxPhotos = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Item Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Taarifa za Item Mkononi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

                TextButton(onClick = viewModel::requestAiValuation) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TerracottaPeach, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (uiState.isEstimatingValue) "Inatathmini..." else "AI Price Check",
                        fontSize = 12.sp,
                        color = TerracottaPeach,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedTextField(
                value = uiState.itemName,
                onValueChange = viewModel::updateItemName,
                label = { Text("Jina la Item (e.g. Sony Bravia 55 TV)") },
                leadingIcon = { Icon(Icons.Default.Devices, contentDescription = null, tint = TerracottaPeach) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Category:", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(listOf("Phones", "Laptops", "TVs & Audio", "Gaming", "Appliances")) { _, cat ->
                    val isSel = uiState.category == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.updateCategory(cat) },
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.brand,
                    onValueChange = viewModel::updateBrand,
                    label = { Text("Brand") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = uiState.condition,
                    onValueChange = viewModel::updateCondition,
                    label = { Text("Condition") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors()
                )
            }

            if (uiState.aiSuggestion != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = DarkSurfaceVariant, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TerracottaPeach)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uiState.aiSuggestion!!, fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Purchase Pricing
            Text("Bei ya Ununuzi & Resale Target", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = uiState.purchasePricePaid,
                    onValueChange = viewModel::updatePurchasePricePaid,
                    label = { Text("Price Paid (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = uiState.estimatedMarketValue,
                    onValueChange = viewModel::updateEstimatedMarketValue,
                    label = { Text("Market Resale (KSh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = textFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Seller Info (Optional)
            Text("Taarifa za Muuzaji (Seller Details - Optional)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.sellerName,
                onValueChange = viewModel::updateSellerName,
                label = { Text("Jina la Muuzaji (Seller Name)") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TerracottaPeach) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.sellerPhone,
                onValueChange = viewModel::updateSellerPhone,
                label = { Text("Phone Number ya Muuzaji") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TerracottaPeach) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes (Accessories / Condition notes)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(uiState.error!!, color = AccentRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::saveDirectPurchase,
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = TextOnTerracotta, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, tint = TextOnTerracotta)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE DIRECT BUY (Ingiza Stock)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextOnTerracotta)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

