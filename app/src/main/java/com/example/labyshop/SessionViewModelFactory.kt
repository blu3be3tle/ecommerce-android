package com.example.labyshop

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel

class SessionViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ) : T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown view model class"
        )
    }
}