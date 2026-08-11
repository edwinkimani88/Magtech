package com.example.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.entities.CustomerEntity
import com.example.data.db.entities.ItemEntity
import com.example.data.db.entities.LoanEntity
import com.example.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CustomerProfileUiState(
    val customer: CustomerEntity? = null,
    val items: List<ItemEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList(),
    val isLoading: Boolean = true
)

class CustomerProfileViewModel(
    private val repository: MagTechRepository,
    private val customerId: Long
) : ViewModel() {

    val uiState: StateFlow<CustomerProfileUiState> = combine(
        repository.getItemsForCustomer(customerId),
        repository.getLoansForCustomer(customerId)
    ) { items, loans ->
        val customer = repository.getCustomerById(customerId)
        CustomerProfileUiState(
            customer = customer,
            items = items,
            loans = loans,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerProfileUiState(isLoading = true)
    )
}
