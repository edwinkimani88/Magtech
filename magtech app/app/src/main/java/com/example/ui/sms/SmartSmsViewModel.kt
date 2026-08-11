package com.example.ui.sms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.db.entities.CustomerEntity
import com.example.data.db.entities.LoanEntity
import com.example.data.db.entities.SmsLogEntity
import com.example.data.repository.MagTechRepository
import com.example.data.sms.SmsManagerService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SmartSmsUiState(
    val customers: List<CustomerEntity> = emptyList(),
    val dueTodayLoans: List<LoanEntity> = emptyList(),
    val overdueLoans: List<LoanEntity> = emptyList(),
    val selectedCustomer: CustomerEntity? = null,
    val selectedLoan: LoanEntity? = null,
    val generatedSmsText: String = "",
    val smsLogs: List<SmsLogEntity> = emptyList(),
    val isGeneratingAi: Boolean = false,
    val isSendingSms: Boolean = false,
    val filterTarget: String = "DUE_TODAY", // "DUE_TODAY", "OVERDUE", "ALL_CUSTOMERS"
    val error: String? = null,
    val sendStatusMessage: String? = null
)

class SmartSmsViewModel(
    application: Application,
    private val repository: MagTechRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : AndroidViewModel(application) {

    private val smsManagerService = SmsManagerService(application)

    private val _uiState = MutableStateFlow(SmartSmsUiState())
    val uiState: StateFlow<SmartSmsUiState> = combine(
        repository.allCustomers,
        repository.getLoansDueToday(),
        repository.getOverdueLoans(),
        repository.allSmsLogs,
        _uiState
    ) { customers, dueToday, overdue, smsLogs, current ->
        current.copy(
            customers = customers,
            dueTodayLoans = dueToday,
            overdueLoans = overdue,
            smsLogs = smsLogs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SmartSmsUiState()
    )

    fun selectTargetFilter(filter: String) {
        _uiState.value = _uiState.value.copy(filterTarget = filter)
    }

    fun selectCustomerAndLoan(customer: CustomerEntity, loan: LoanEntity?) {
        _uiState.value = _uiState.value.copy(
            selectedCustomer = customer,
            selectedLoan = loan
        )
        generateAiMessage()
    }

    fun updateGeneratedText(text: String) {
        _uiState.value = _uiState.value.copy(generatedSmsText = text)
    }

    fun generateAiMessage() {
        val state = _uiState.value
        val customer = state.selectedCustomer ?: return
        val loan = state.selectedLoan ?: state.dueTodayLoans.firstOrNull { it.customerId == customer.id }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingAi = true)

            val balance = loan?.let { it.totalPayable - it.paidAmount } ?: 0.0
            val itemName = loan?.let { repository.getItemById(it.itemId)?.itemName } ?: "Item yako"

            val message = aiService.generateSmsReminder(
                customerName = customer.fullName,
                itemName = itemName,
                balancePayable = balance,
                dueDateFormatted = if (loan?.status == "DUE_TODAY") "LEO" else "punde",
                urgency = state.filterTarget
            )

            _uiState.value = _uiState.value.copy(
                isGeneratingAi = false,
                generatedSmsText = message
            )
        }
    }

    fun sendSmsNow() {
        val state = _uiState.value
        val customer = state.selectedCustomer ?: return
        val text = state.generatedSmsText

        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingSms = true)

            val success = smsManagerService.sendSmsDirectOrIntent(customer.phoneNumber, text)
            val status = if (success) "SENT" else "SIMULATED"

            repository.logSms(customer.id, customer.phoneNumber, text, status)

            _uiState.value = _uiState.value.copy(
                isSendingSms = false,
                sendStatusMessage = "SMS imetumwa kwa ${customer.fullName} (${customer.phoneNumber})"
            )
        }
    }
}
