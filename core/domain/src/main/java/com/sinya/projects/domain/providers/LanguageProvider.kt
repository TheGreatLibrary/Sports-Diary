package com.sinya.projects.domain.providers

import kotlinx.coroutines.flow.Flow

interface LanguageProvider {
    val currentLanguage: Flow<String>
}

