package com.sinya.projects.sportsdiary.data.datastore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val themeMode = dataStoreManager.getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val language = dataStoreManager.getLangMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = "ru"
        )

    val planId = dataStoreManager.getPlanMorningId()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = 0
        )

    fun toggleTheme() {
        viewModelScope.launch {
            dataStoreManager.setThemeMode(!themeMode.value)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            dataStoreManager.setLangMode(lang)
        }
    }

    fun setPlanMorningId(id: Int) {
        viewModelScope.launch {
            dataStoreManager.setPlanMorningId(id)
        }
    }
}