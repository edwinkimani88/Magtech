package com.example.ui.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DirectPurchaseUiState(
    val itemName: String = "",
    val category: String = "Phones",
    val brand: String = "Samsung",
    val condition: String = "Like New",
    val estimatedMarketValue: String = "",
    val purchasePricePaid: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val notes: String = "",
    val photoUrls: List<String> = emptyList(),
    val shopLocation: String = "Shop 1", // "Shop 1" or "Shop 2"
    val isSaving: Boolean = false,
    val isEstimatingValue: Boolean = false,
    val aiSuggestion: String? = null,
    val successItemId: Long? = null,
    val error: String? = null
)

class DirectPurchaseViewModel(
    private val repository: MagTechRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DirectPurchaseUiState())
    val uiState: StateFlow<DirectPurchaseUiState> = _uiState.asStateFlow()

    fun updateItemName(name: String) { _uiState.value = _uiState.value.copy(itemName = name) }
    fun updateCategory(cat: String) { _uiState.value = _uiState.value.copy(category = cat) }
    fun updateBrand(brand: String) { _uiState.value = _uiState.value.copy(brand = brand) }
    fun updateCondition(cond: String) { _uiState.value = _uiState.value.copy(condition = cond) }
    fun updateEstimatedMarketValue(valStr: String) { _uiState.value = _uiState.value.copy(estimatedMarketValue = valStr) }
    fun updatePurchasePricePaid(valStr: String) { _uiState.value = _uiState.value.copy(purchasePricePaid = valStr) }
    fun updateSellerName(name: String) { _uiState.value = _uiState.value.copy(sellerName = name) }
    fun updateSellerPhone(phone: String) { _uiState.value = _uiState.value.copy(sellerPhone = phone) }
    fun updateNotes(notes: String) { _uiState.value = _uiState.value.copy(notes = notes) }
    fun updateShopLocation(shop: String) { _uiState.value = _uiState.value.copy(shopLocation = shop) }

    fun updatePhotos(photos: List<String>) {
        _uiState.value = _uiState.value.copy(photoUrls = photos)
    }

    fun requestAiValuation() {
        val state = _uiState.value
        if (state.itemName.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isEstimatingValue = true)
            val result = aiService.estimateMarketValue(
                itemName = state.itemName,
                category = state.category,
                brand = state.brand,
                condition = state.condition
            )
            _uiState.value = _uiState.value.copy(
                isEstimatingValue = false,
                estimatedMarketValue = result.marketValue.toInt().toString(),
                purchasePricePaid = result.forcedSaleValue.toInt().toString(),
                aiSuggestion = result.analysis
            )
        }
    }

    fun saveDirectPurchase() {
        val state = _uiState.value

        if (state.itemName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Boss, ingiza jina la item!")
            return
        }

        val pricePaid = state.purchasePricePaid.toDoubleOrNull() ?: 0.0
        val marketVal = state.estimatedMarketValue.toDoubleOrNull() ?: (pricePaid * 1.3)

        if (pricePaid <= 0) {
            _uiState.value = _uiState.value.copy(error = "Weka Bei ya Ununuzi (Purchase Price Paid)!")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val itemId = repository.registerDirectPurchase(
                    itemName = state.itemName,
                    category = state.category,
                    brand = state.brand,
                    condition = state.condition,
                    estimatedMarketValue = marketVal,
                    purchasePrice = pricePaid,
                    notes = state.notes,
                    photoUrls = state.photoUrls,
                    sellerName = state.sellerName,
                    sellerPhone = state.sellerPhone,
                    shopLocation = state.shopLocation
                )
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successItemId = itemId,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Imefail ku-save: ${e.localizedMessage}"
                )
            }
        }
    }
}
