package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.models.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "magtech_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_FULL_NAME = stringPreferencesKey("full_name")
        private val KEY_SHOP_ID = stringPreferencesKey("shop_id")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_SHOP_FILTER = stringPreferencesKey("shop_filter")
        private val KEY_CUSTOM_ADMINS_JSON = stringPreferencesKey("custom_admins_json")
    }

    val customAdminsJsonFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_ADMINS_JSON]
    }

    suspend fun saveCustomAdminJson(json: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_CUSTOM_ADMINS_JSON] = json
        }
    }

    val userSessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val userId = prefs[KEY_USER_ID]
        val email = prefs[KEY_EMAIL]
        val fullName = prefs[KEY_FULL_NAME]
        val shopId = prefs[KEY_SHOP_ID]
        val token = prefs[KEY_TOKEN]

        if (!userId.isNullOrEmpty() && !email.isNullOrEmpty() && !shopId.isNullOrEmpty()) {
            UserSession(
                userId = userId,
                email = email,
                fullName = fullName ?: "Admin",
                shopId = shopId,
                token = token ?: "valid_token"
            )
        } else {
            null
        }
    }

    val selectedShopFilterFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOP_FILTER] ?: "all"
    }

    suspend fun saveUserSession(session: UserSession) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = session.userId
            prefs[KEY_EMAIL] = session.email
            prefs[KEY_FULL_NAME] = session.fullName
            prefs[KEY_SHOP_ID] = session.shopId
            prefs[KEY_TOKEN] = session.token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_FULL_NAME)
            prefs.remove(KEY_SHOP_ID)
            prefs.remove(KEY_TOKEN)
        }
    }

    suspend fun saveShopFilter(shopId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOP_FILTER] = shopId
        }
    }
}
