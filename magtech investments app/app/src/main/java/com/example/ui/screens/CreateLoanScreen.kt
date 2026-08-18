package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.ui.components.MagTechTopAppBar
import com.example.ui.theme.MagTechAccentGold
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLoanScreen(
    viewModel: MagTechViewModel,
    onNavigateHome: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val shopFilter by viewModel.shopFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    val currentBranchName = when (if (shopFilter == "all") userSession?.shopId ?: "shop_1" else shopFilter) {
        "shop_2" -> "Kitengela - Deliverance Rd Branch"
        else -> "Kitengela - Chairman Rd Branch"
    }

    // Step 1: Customer Details State
    var customerName by remember { mutableStateOf("") }
    var customerIdNumber by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }

    // Step 2: Financials State
    var loanAmountText by remember { mutableStateOf("") }
    var selectedInterestRate by remember { mutableStateOf(20) } // Default 20%
    var customInterestText by remember { mutableStateOf("") }

    // Duration State
    var selectedDurationDays by remember { mutableStateOf(14) } // Default 14 days
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var dueDate by remember(selectedDurationDays) {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, selectedDurationDays) }
        mutableStateOf(dateFormat.format(cal.time))
    }

    // Calculated Payable Amount
    val principalAmount = loanAmountText.toDoubleOrNull() ?: 0.0
    val interestPercentage = if (selectedInterestRate > 0) selectedInterestRate.toDouble() else (customInterestText.toDoubleOrNull() ?: 0.0)
    val interestAmount = principalAmount * (interestPercentage / 100.0)
    val totalPayableAmount = principalAmount + interestAmount

    // Step 3: Collateral Item State
    val categories = listOf("Phones", "Laptops", "TVs", "Audio", "Fridges", "Cookers", "Home Appliances", "Kitchen", "Gaming", "Accessories", "Other")
    var selectedCategory by remember { mutableStateOf("Phones") }

    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    val conditions = listOf("LIKE NEW", "GOOD", "FAIR")
    var selectedCondition by remember { mutableStateOf("GOOD") }

    // Step 4: Photography State
    var photoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            photoUris = photoUris + currentPhotoUri!!
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val remainingSlots = 4 - photoUris.size
            if (remainingSlots > 0) {
                photoUris = photoUris + uris.take(remainingSlots)
            }
        }
    }

    fun executeCameraLaunch() {
        if (photoUris.size >= 4) return
        try {
            val photoFile = File.createTempFile("COLLATERAL_${System.currentTimeMillis()}_", ".jpg", context.cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            currentPhotoUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            executeCameraLaunch()
        }
    }

    fun launchCamera() {
        if (photoUris.size >= 4) return
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            executeCameraLaunch()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Validation State
    val isCustomerValid = customerName.isNotBlank() && customerPhone.isNotBlank() && customerIdNumber.isNotBlank()
    val isFinancialsValid = principalAmount > 0
    val isCollateralValid = itemName.isNotBlank()
    val isFormReady = isCustomerValid && isFinancialsValid && isCollateralValid

    Scaffold(
        topBar = {
            MagTechTopAppBar(
                title = "New Loan Entry",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Branch Identifier Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00C8A8).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF00C8A8))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ISSUING BRANCH LOCATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00C8A8)
                        )
                        Text(
                            text = currentBranchName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // STEP 1: CUSTOMER DETAILS CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonPin, contentDescription = null, tint = MagTechTealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1. Customer Identity", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Full Name *") },
                        placeholder = { Text("e.g. Peter Kamau Njoroge") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number *") },
                            placeholder = { Text("0712345678") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = customerIdNumber,
                            onValueChange = { customerIdNumber = it },
                            label = { Text("ID Number *") },
                            placeholder = { Text("28374920") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // STEP 2: FINANCIALS & INTEREST CALCULATOR CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MagTechTealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("2. Loan & Interest Calculator", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    OutlinedTextField(
                        value = loanAmountText,
                        onValueChange = { loanAmountText = it },
                        label = { Text("Amount Handed Out / Principal (KSh) *") },
                        placeholder = { Text("e.g. 10000") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Interest Rate Selector Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Select Interest Rate:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 20, 25, 30).forEach { rate ->
                                FilterChip(
                                    selected = selectedInterestRate == rate,
                                    onClick = { selectedInterestRate = rate },
                                    label = { Text("$rate%") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Loan Duration Selector Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Select Loan Duration:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(7 to "7 Days", 14 to "14 Days", 21 to "21 Days", 30 to "30 Days").forEach { (days, label) ->
                                FilterChip(
                                    selected = selectedDurationDays == days,
                                    onClick = { selectedDurationDays = days },
                                    label = { Text(label) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Financial Summary Calculation Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDFA),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF99F6E4)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Principal Amount:", fontSize = 13.sp, color = Color.DarkGray)
                                Text("KSh ${String.format("%,.0f", principalAmount)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Interest Fee ($selectedInterestRate%):", fontSize = 13.sp, color = MagTechAccentGold)
                                Text("+ KSh ${String.format("%,.0f", interestAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagTechAccentGold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Customer Payable:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MagTechTealDark)
                                Text("KSh ${String.format("%,.0f", totalPayableAmount)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MagTechTealDark)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Due Date:", fontSize = 12.sp, color = Color.Gray)
                                Text("$dueDate ($selectedDurationDays days)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MagTechTealPrimary)
                            }
                        }
                    }
                }
            }

            // STEP 3: COLLATERAL ITEM DETAILS CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Devices, contentDescription = null, tint = MagTechTealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("3. Collateral Item Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Text("Item Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name & Model *") },
                        placeholder = { Text("e.g. Tecno Camon 20 Pro") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = itemDescription,
                        onValueChange = { itemDescription = it },
                        label = { Text("Serial No / Specifications / Notes") },
                        placeholder = { Text("e.g. IMEI 358129304..., 256GB, Black, includes charger") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Item Condition:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        conditions.forEach { cond ->
                            FilterChip(
                                selected = selectedCondition == cond,
                                onClick = { selectedCondition = cond },
                                label = { Text(cond) }
                            )
                        }
                    }
                }
            }

            // STEP 4: PHOTOGRAPHY CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MagTechTealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("4. Piga Picha (Collateral Photos)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MagTechTealDark.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${photoUris.size}/4 Photos",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MagTechTealDark
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { launchCamera() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C8A8), contentColor = MagTechTealDark),
                            shape = RoundedCornerShape(10.dp),
                            enabled = photoUris.size < 4,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            enabled = photoUris.size < 4,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    if (photoUris.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(photoUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, MagTechTealPrimary, RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = "Collateral Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    IconButton(
                                        onClick = { photoUris = photoUris - uri },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .background(Color.Red, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "💡 Pro Tip: Taking 2+ photos of the item builds verification proof for MagTech records.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Form Readiness Bar
            if (!isFormReady) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF92400E))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Please enter Customer Name, Phone, ID Number, Loan Amount, and Collateral Item Name.",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Submit Button
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MagTechTealPrimary)
                }
            } else {
                Button(
                    onClick = {
                        if (isFormReady) {
                            viewModel.createLoan(
                                customerName = customerName,
                                customerIdNumber = customerIdNumber,
                                customerPhone = customerPhone,
                                loanAmount = principalAmount,
                                amountPayable = totalPayableAmount,
                                dueDate = dueDate,
                                notes = "Collateral: $itemName ($selectedCondition)",
                                itemCategory = selectedCategory,
                                itemName = itemName,
                                itemDescription = itemDescription,
                                itemCondition = selectedCondition,
                                photoUrls = photoUris.map { it.toString() },
                                onSuccess = onNavigateHome
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormReady) MagTechTealDark else Color.Gray,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = isFormReady,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SAVE LOAN TO SUPABASE DATABASE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
