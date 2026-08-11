package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedRole by remember { mutableStateOf("Admin — Shop 1") }
    var pinInput by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Terracotta Brand Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(TerracottaPeach)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "MagTech",
                            tint = TerracottaPeach,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "MAGTECH INVESTMENTS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextOnTerracotta,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Smart Loans • Electronics • Shared Marketplace",
                        fontSize = 12.sp,
                        color = Color(0xFF3C2010),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Niaje Boss, Ingia System",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Chagua shop uweze ku-manage inventory na loans",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Admin Account Selector (Shop 1 vs Shop 2)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "Admin — Shop 1" to "Westlands Branch",
                    "Admin — Shop 2" to "CBD Branch"
                ).forEach { (role, branch) ->
                    val isSelected = selectedRole == role
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) DarkSurfaceVariant else DarkSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TerracottaPeach else DarkBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedRole = role }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedRole = role },
                                    colors = RadioButtonDefaults.colors(selectedColor = TerracottaPeach)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = role,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = branch,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (role.contains("1")) Color(0xFF1E3A8A) else Color(0xFF065F46))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (role.contains("1")) "Shop 1" else "Shop 2",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // PIN Input Field
            OutlinedTextField(
                value = pinInput,
                onValueChange = { if (it.length <= 4) pinInput = it },
                label = { Text("Security PIN (e.g. 1234)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerracottaPeach,
                    unfocusedBorderColor = DarkBorder,
                    focusedLabelColor = TerracottaPeach,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PIN",
                        tint = TerracottaPeach
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = AccentRed,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = TerracottaPeach)
                )
                Text(
                    text = "Baki logged in kwenye simu hii (Remember Me)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val pin = if (pinInput.isBlank()) "1234" else pinInput
                    if (viewModel.loginWithPin(pin, selectedRole, rememberMe)) {
                        onLoginSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPeach),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "INGIA SYSTEM",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnTerracotta
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Sign In shortcut
            OutlinedButton(
                onClick = {
                    viewModel.loginWithPin("1234", selectedRole, true)
                    onLoginSuccess()
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometrics",
                    tint = TerracottaPeach
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quick Admin Sign In",
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
