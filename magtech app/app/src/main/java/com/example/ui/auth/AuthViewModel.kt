package com.example.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userRole: String = "Admin — Shop 1", // "Admin — Shop 1" or "Admin — Shop 2"
    val userName: String = "Admin Shop 1 (Westlands)",
    val defaultShop: String = "Shop 1", // "Shop 1" or "Shop 2"
    val pin: String = "",
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("magtech_auth_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        AuthState(
            isLoggedIn = prefs.getBoolean("is_logged_in", false),
            userRole = prefs.getString("user_role", "Admin — Shop 1") ?: "Admin — Shop 1",
            userName = prefs.getString("user_name", "Admin Shop 1 (Westlands)") ?: "Admin Shop 1 (Westlands)",
            defaultShop = prefs.getString("default_shop", "Shop 1") ?: "Shop 1"
        )
    )
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun loginWithPin(enteredPin: String, selectedRole: String, rememberMe: Boolean = true): Boolean {
        if (enteredPin == "1234" || enteredPin.length == 4) {
            val shop = if (selectedRole.contains("Shop 2")) "Shop 2" else "Shop 1"
            val name = if (shop == "Shop 1") "Admin Shop 1 (Westlands)" else "Admin Shop 2 (CBD)"

            _uiState.value = _uiState.value.copy(
                isLoggedIn = true,
                userRole = selectedRole,
                userName = name,
                defaultShop = shop,
                error = null
            )
            if (rememberMe) {
                prefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("user_role", selectedRole)
                    .putString("user_name", name)
                    .putString("default_shop", shop)
                    .apply()
            }
            return true
        } else {
            _uiState.value = _uiState.value.copy(error = "Security PIN si sahihi. Jaribu '1234'")
            return false
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _uiState.value = AuthState(isLoggedIn = false)
    }
}
