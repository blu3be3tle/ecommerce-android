package com.example.labyshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess

    fun login(
        identifier: String,
        password: String,
        ) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            _loginSuccess.value = false

            try {
                repository.login(
                    identifier = identifier,
                    password = password
                )
                _loginSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            }

            catch (e: Exception) {
                _loginError.value = e.localizedMessage ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            _logoutSuccess.value = false
            _loginError.value = null

            try {
                repository.logout()
                _logoutSuccess.value = true
            } catch (e: CancellationException) {
                throw e
            }

            catch (e: Exception) {
                _loginError.value = e.localizedMessage ?: "Logout failed"
                } finally {
                _isLoading.value = false
            }
        }
    }


}