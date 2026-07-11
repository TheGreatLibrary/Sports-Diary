package com.sinya.projects.sportsdiary.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Changelog(
    val version: String,
    val date: String,
    val locales: Map<String, LocaleChanges>
)

@Serializable
data class LocaleChanges(
    val changes: List<String>
)

@Serializable
data class ChangelogResponse(
    val releases: List<Changelog>
)
