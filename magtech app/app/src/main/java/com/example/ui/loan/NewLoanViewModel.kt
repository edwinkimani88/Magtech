package com.example.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class NewLoanUiState(
    val customerName: String = "",
    val nationalId: String = "",
    val phoneNumber: String = "",
    val itemName: String = "",
    val category: String = "Phones",
    val brand: String = "Samsung",
    val condition: String = "Like New",
    val estimatedMarketValue: String = "",
    val forcedSaleValue: String = "",
    val loanAmountGiven: String = "",
    val totalAmountPayable: String = "",
    val dueDateDays: Int = 14,
    val notes: String = "",
    val photoUrls: List<String> = emptyList(),
    val shopLocation: String = "Shop 1", // "Shop 1" or "Shop 2"
    val isEstimatingValue: Boolean = false,
    val isSaving: Boolean = false,
    val aiSuggestion: String? = null,
    val successLoanId: Long? = null,
    val error: String? = null
)

class NewLoanViewModel(
    private val repository: MagTechRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewLoanUiState())
    val uiState: StateFlow<NewLoanUiState> = _uiState.asStateFlow()

    fun updateCustomerName(name: String) { _uiState.value = _uiState.value.copy(customerName = name) }
    fun updateNationalId(id: String) { _uiState.value = _uiState.value.copy(nationalId = id) }
    fun updatePhoneNumber(phone: String) { _uiState.value = _uiState.value.copy(phoneNumber = phone) }
    fun updateItemName(name: String) { _uiState.value = _uiState.value.copy(itemName = name) }
    fun updateCategory(cat: String) { _uiState.value = _uiState.value.copy(category = cat) }
    fun updateBrand(brand: String) { _uiState.value = _uiState.value.copy(brand = brand) }
    fun updateCondition(cond: String) { _uiState.value = _uiState.value.copy(condition = cond) }
    fun updateEstimatedMarketValue(valStr: String) { _uiState.value = _uiState.value.copy(estimatedMarketValue = valStr) }
    fun updateForcedSaleValue(valStr: String) { _uiState.value = _uiState.value.copy(forcedSaleValue = valStr) }
    fun updateLoanAmountGiven(valStr: String) {
        val given = valStr.toDoubleOrNull() ?: 0.0
        val defaultPayable = if (given > 0) (given * 1.15).toInt().toString() else ""
        _uiState.value = _uiState.value.copy(
            loanAmountGiven = valStr,
            totalAmountPayable = if (_uiState.value.totalAmountPayable.isBlank()) defaultPayable else _uiState.value.totalAmountPayable
        )
    }
    fun updateTotalAmountPayable(valStr: String) { _uiState.value = _uiState.value.copy(totalAmountPayable = valStr) }
    fun updateDueDateDays(days: Int) { _uiState.value = _uiState.value.copy(dueDateDays = days) }
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
                forcedSaleValue = result.forcedSaleValue.toInt().toString(),
                loanAmountGiven = (result.forcedSaleValue * 0.8).toInt().toString(),
                totalAmountPayable = (result.forcedSaleValue * 0.95).toInt().toString(),
                aiSuggestion = result.analysis
            )
        }
    }

    fun saveLoan() {
        val state = _uiState.value

        if (state.customerName.isBlank() || state.nationalId.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Boss, jaza jina la mteja, Kitambulisho (ID) na Namba ya Simu!")
            return
        }
        if (state.itemName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Weka jina la item Boss!")
            return
        }

        val given = state.loanAmountGiven.toDoubleOrNull() ?: 0.0
        val payable = state.totalAmountPayable.toDoubleOrNull() ?: (given * 1.15)
        val marketVal = state.estimatedMarketValue.toDoubleOrNull() ?: (given * 1.5)
        val forcedVal = state.forcedSaleValue.toDoubleOrNull() ?: (given * 1.2)

        if (given <= 0) {
            _uiState.value = _uiState.value.copy(error = "Weka Loan Amount halali!")
            return
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, state.dueDateDays)
        val dueDateMs = calendar.timeInMillis

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val loanId = repository.registerNewLoan(
                    customerName = state.customerName,
                    nationalId = state.nationalId,
                    phoneNumber = state.phoneNumber,
                    itemName = state.itemName,
                    category = state.category,
                    brand = state.brand,
                    condition = state.condition,
                    estimatedMarketValue = marketVal,
                    forcedSaleValue = forcedVal,
                    loanAmountGiven = given,
                    totalAmountPayable = payable,
                    dueDateMs = dueDateMs,
                    notes = state.notes,
                    photoUrls = state.photoUrls,
                    shopLocation = state.shopLocation
                )
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    successLoanId = loanId,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Imefail ku-save loan: ${e.localizedMessage}"
                )
            }
        }
    }
}
