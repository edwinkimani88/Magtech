package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MagTechTealDark
import com.example.ui.theme.MagTechTealPrimary
import com.example.ui.viewmodel.MagTechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MagTechViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("admin1@magtech.co.ke") }
    var password by remember { mutableStateOf("magtech2026") }
    var selectedShopRole by remember { mutableStateOf("shop_1") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.uiMessage.collectAsState()

    var showRegisterDialog by remember { mutableStateOf(false) }
    var regEmail by remember { mutableStateOf("") }
    var regFullName by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regShopId by remember { mutableStateOf("shop_1") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MagTechTealDark,
                        Color(0xFF041E1C),
                        MagTechTealDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Brand Logo Box
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C8A8).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color(0xFF00C8A8),
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MAGTECH INVESTMENTS",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Kitengela Chairman Rd & Deliverance Rd Branches",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF00C8A8)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Admin Selection Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedShopRole = "shop_1"
                        email = "admin1@magtech.co.ke"
                        password = "magtech2026"
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedShopRole == "shop_1") Color(0xFF00C8A8) else Color.Transparent,
                        contentColor = if (selectedShopRole == "shop_1") MagTechTealDark else Color.White
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Admin (Chairman Rd)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        selectedShopRole = "shop_2"
                        email = "admin2@magtech.co.ke"
                        password = "magtech2026"
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedShopRole == "shop_2") Color(0xFF00C8A8) else Color.Transparent,
                        contentColor = if (selectedShopRole == "shop_2") MagTechTealDark else Color.White
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Admin (Deliverance Rd)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Admin Email", color = Color.White.copy(0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF00C8A8)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C8A8),
                            unfocusedBorderColor = Color.White.copy(0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Unique Admin Password", color = Color.White.copy(0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00C8A8)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00C8A8),
                            unfocusedBorderColor = Color.White.copy(0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF00C8A8))
                    } else {
                        Button(
                            onClick = {
                                viewModel.loginAdmin(email, password, onLoginSuccess)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C8A8), contentColor = MagTechTealDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("LOGIN TO MAGTECH SYSTEM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { showRegisterDialog = true }
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFF00C8A8), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Set Custom Admin Password / Register Account",
                            color = Color(0xFF00C8A8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Connected to Supabase Real-Time Database",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        // Register / Set Custom Password Dialog
        if (showRegisterDialog) {
            AlertDialog(
                onDismissRequest = { showRegisterDialog = false },
                title = {
                    Text("Create Unique Admin Password", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enter admin details and set your unique custom password for system access:", fontSize = 13.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Admin Email *") },
                            placeholder = { Text("e.g. boss@magtech.co.ke") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Full Name *") },
                            placeholder = { Text("e.g. John Kitengela Admin") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Assigned Kitengela Branch:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = regShopId == "shop_1",
                                onClick = { regShopId = "shop_1" },
                                label = { Text("Chairman Rd") }
                            )
                            FilterChip(
                                selected = regShopId == "shop_2",
                                onClick = { regShopId = "shop_2" },
                                label = { Text("Deliverance Rd") }
                            )
                        }

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("New Unique Password *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (regEmail.isNotBlank() && regPassword.length >= 4) {
                                viewModel.registerAdminAccount(
                                    email = regEmail,
                                    pass = regPassword,
                                    fullName = regFullName,
                                    shopId = regShopId,
                                    onSuccess = {
                                        email = regEmail
                                        password = regPassword
                                        selectedShopRole = regShopId
                                        showRegisterDialog = false
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagTechTealDark)
                    ) {
                        Text("SAVE PASSWORD & ACCOUNT")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRegisterDialog = false }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}
