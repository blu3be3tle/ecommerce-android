package com.example.labyshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    private val repository: AuthRepository
): ViewModel() {
    private val _status = MutableStateFlow(SessionStatus.CHECKING)
    val status: StateFlow<SessionStatus> = _status


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    fun checkSession() {

        viewModelScope.launch {
            _status.value = SessionStatus.CHECKING
            _error.value = null

            try {
                if (repository.hasValidSession()) {
                    _status.value = SessionStatus.AUTHENTICATED
                } else {
                    _status.value = SessionStatus.UNAUTHENTICATED
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _status.value = SessionStatus.ERROR
                _error.value = e.localizedMessage ?: "Unable to check your session. Please try again!"
            }
        }
    }
}