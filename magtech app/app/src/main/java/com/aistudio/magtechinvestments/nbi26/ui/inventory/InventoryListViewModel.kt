package com.aistudio.magtechinvestments.nbi26.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.magtechinvestments.nbi26.data.db.entities.ItemEntity
import com.aistudio.magtechinvestments.nbi26.data.repository.MagTechRepository
import kotlinx.coroutines.flow.*

data class InventoryListUiState(
    val items: List<ItemEntity> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class InventoryListViewModel(private val repository: MagTechRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<InventoryListUiState> = combine(
        repository.allItems,
        _selectedFilter,
        _searchQuery
    ) { allItems, filter, query ->
        val filtered = allItems.filter { item ->
            val matchesFilter = when (filter) {
                "Active Loans" -> item.status == "Active Loan"
                "Purchased" -> item.status == "Purchased"
                "Redeemed" -> item.status == "Redeemed"
                "Listed Marketplace" -> item.isPublishedToMarketplace
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    item.itemName.contains(query, ignoreCase = true) ||
                    item.brand.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
        InventoryListUiState(
            items = filtered,
            selectedFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryListUiState(isLoading = true)
    )

    fun setFilter(filter: String) { _selectedFilter.value = filter }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
}
