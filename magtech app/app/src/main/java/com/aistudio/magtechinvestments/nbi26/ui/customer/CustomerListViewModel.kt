package com.aistudio.magtechinvestments.nbi26.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.magtechinvestments.nbi26.data.db.entities.CustomerEntity
import com.aistudio.magtechinvestments.nbi26.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*

data class CustomerListUiState(
    val customers: List<CustomerEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class CustomerListViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<CustomerListUiState> = combine(
        repository.allCustomers,
        _searchQuery
    ) { allCustomers, query ->
        val filtered = allCustomers.filter { customer ->
            query.isBlank() ||
                    customer.fullName.contains(query, ignoreCase = true) ||
                    customer.nationalId.contains(query, ignoreCase = true) ||
                    customer.phoneNumber.contains(query, ignoreCase = true)
        }
        CustomerListUiState(
            customers = filtered,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CustomerListUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) { _searchQuery.value = query }
}
