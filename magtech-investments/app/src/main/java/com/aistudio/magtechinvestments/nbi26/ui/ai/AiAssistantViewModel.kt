package com.aistudio.magtechinvestments.nbi26.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.magtechinvestments.nbi26.data.ai.GeminiAiService
import com.aistudio.magtechinvestments.nbi26.data.repository.MagTechRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiAssistantUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = "AI",
            text = "Sasa Boss! Mimi ni MagTech AI Assistant. Unaweza kuniuliza bei ya market ya simu/laptops, au kuhusu stock na loans za shop!"
        )
    ),
    val currentInput: String = "",
    val isLoading: Boolean = false
)

class AiAssistantViewModel(
    private val repository: MagTechRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun updateInput(input: String) {
        _uiState.value = _uiState.value.copy(currentInput = input)
    }

    fun sendMessage(userText: String = _uiState.value.currentInput) {
        if (userText.isBlank()) return

        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(ChatMessage("USER", userText))

        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            currentInput = "",
            isLoading = true
        )

        viewModelScope.launch {
            val totalItems = repository.totalItemCount
            val shopContext = "MagTech Shop: Total Items in stock. Ready to serve Nairobi electronics business."

            val aiResponse = aiService.askBusinessAssistant(userText, shopContext)

            val updatedMessages = _uiState.value.messages.toMutableList()
            updatedMessages.add(ChatMessage("AI", aiResponse))

            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                isLoading = false
            )
        }
    }
}

