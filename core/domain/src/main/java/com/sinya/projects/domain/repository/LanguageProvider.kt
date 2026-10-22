package com.sinya.projects.domain.repository

import kotlinx.coroutines.flow.Flow

interface LanguageProvider {
    val currentLanguage: Flow<String>
}