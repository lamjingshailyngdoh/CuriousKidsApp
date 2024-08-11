package com.lyngdoh.curiouskids

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
       // modelName = "gemini-pro-vision",
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        bitmap: Bitmap?,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        if (bitmap != null) {
                            image(bitmap)
                        }
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    val mainObjectName = extractMainObjectName(outputContent)
                    _uiState.value = UiState.Success(mainObjectName)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun extractMainObjectName(responseText: String): String {
        // Extract the main object name from the response text
        // This is a simple implementation and might need adjustments based on actual response format
        val lines = responseText.lines()
        return if (lines.isNotEmpty()) {
            lines[0] // Assuming the main object name is on the first line
        } else {
            "Unknown object"
        }
    }
}
