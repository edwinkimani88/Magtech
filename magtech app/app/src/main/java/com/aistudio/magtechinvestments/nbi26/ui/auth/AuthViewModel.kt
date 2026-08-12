package com.aistudio.magtechinvestments.nbi26.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.magtechinvestments.nbi26.data.db.MagTechDatabase
import com.aistudio.magtechinvestments.nbi26.data.repository.MagTechRepository
import com.aistudio.magtechinvestments.nbi26.data.supabase.SupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoggedIn: Boolean = false,
    val userRole: String = "Admin — Shop 1", // "Admin — Shop 1" or "Admin — Shop 2"
    val userName: String = "Admin Shop 1 (Chairman Road)",
    val defaultShop: String = "Shop 1", // "Shop 1" or "Shop 2"
    val pin: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("magtech_auth_prefs", Context.MODE_PRIVATE)
    private val supabaseService = SupabaseService(application)
    private val database = MagTechDatabase.getDatabase(application, viewModelScope)
    private val repository = MagTechRepository(
        itemDao = database.itemDao(),
        customerDao = database.customerDao(),
        loanDao = database.loanDao(),
        smsLogDao = database.smsLogDao(),
        transactionDao = database.transactionDao(),
        supabaseService = supabaseService
    )

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val token = supabaseService.getPersistedSessionToken()
        if (isLoggedIn && !token.isNullOrBlank()) {
            val role = prefs.getString("user_role", "Admin — Shop 1") ?: "Admin — Shop 1"
            val shop = if (role.contains("Shop 2")) "Shop 2" else "Shop 1"
            val name = if (shop == "Shop 1") "Admin Shop 1 (Chairman Road)" else "Admin Shop 2 (Deliverance Road)"
            _uiState.value = AuthState(
                isLoggedIn = true,
                userRole = role,
                userName = name,
                defaultShop = shop
            )
            // Trigger background cloud sync on app start
            viewModelScope.launch {
                repository.syncAllDataFromCloud()
            }
        } else {
            _uiState.value = AuthState(isLoggedIn = false)
        }
    }

    fun loginWithPin(enteredPin: String, selectedRole: String, rememberMe: Boolean = true, onResult: (Boolean) -> Unit) {
        if (enteredPin.isBlank() || enteredPin.length < 4) {
            _uiState.value = _uiState.value.copy(error = "Tafadhali ingiza PIN kamili ya tarakimu 4")
            onResult(false)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val shop = if (selectedRole.contains("Shop 2")) "Shop 2" else "Shop 1"
            val name = if (shop == "Shop 1") "Admin Shop 1 (Chairman Road)" else "Admin Shop 2 (Deliverance Road)"

            val (success, errorMsg) = supabaseService.authenticateAdmin(selectedRole, enteredPin)

            if (success) {
                if (rememberMe) {
                    prefs.edit()
                        .putBoolean("is_logged_in", true)
                        .putString("user_role", selectedRole)
                        .putString("user_name", name)
                        .putString("default_shop", shop)
                        .apply()
                }

                // Restore all cloud data (if phone lost or changed)
                repository.syncAllDataFromCloud()

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    userRole = selectedRole,
                    userName = name,
                    defaultShop = shop,
                    error = null,
                    isLoading = false
                )
                onResult(true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMsg ?: "PIN sio sahihi. Authentication imekataliwa na Supabase."
                )
                onResult(false)
            }
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        supabaseService.clearSession()
        _uiState.value = AuthState(isLoggedIn = false)
    }
}

