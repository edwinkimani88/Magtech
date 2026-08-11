package com.example.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.CustomerEntity
import com.example.data.db.entities.ItemEntity
import com.example.data.db.entities.LoanEntity
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.first

data class ItemDetailUiState(
    val item: ItemEntity? = null,
    val customer: CustomerEntity? = null,
    val loan: LoanEntity? = null,
    val isLoading: Boolean = true,
    val isRecordingPayment: Boolean = false,
    val error: String? = null
)

class ItemDetailViewModel(
    private val repository: MagTechRepository,
    private val itemId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    init {
        loadItemDetails()
    }

    fun loadItemDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val item = repository.getItemById(itemId)
            if (item != null) {
                val customer = item.customerId?.let { repository.getCustomerById(it) }
                val loan = repository.allLoans.first().firstOrNull { it.itemId == itemId }
                _uiState.value = _uiState.value.copy(
                    item = item,
                    customer = customer,
                    loan = loan,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Item haikupatikana!")
            }
        }
    }

    fun recordRepayment(amount: Double) {
        val loan = _uiState.value.loan ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRecordingPayment = true)
            repository.recordLoanPayment(loan.id, amount)
            loadItemDetails()
        }
    }

    fun toggleMarketplace(isPublished: Boolean) {
        viewModelScope.launch {
            repository.toggleMarketplacePublish(itemId, isPublished)
            loadItemDetails()
        }
    }
}
