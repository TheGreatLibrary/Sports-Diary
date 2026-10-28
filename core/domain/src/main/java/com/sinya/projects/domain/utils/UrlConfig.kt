package com.sinya.projects.domain.utils

object UrlConfig {
    const val config = "https://raw.githubusercontent.com/TheGreatLibrary/SportDiaryChanges/main/changelog.json"
    const val marketRuStore = "https://www.rustore.ru/catalog/app/com.sinya.projects.sportsdiary"
    const val healthAPK = "market://details?id=com.google.android.apps.healthdata"

    fun getGif(gifPath: String) = "https://raw.githubusercontent.com/hasaneyldrm/exercises-dataset/main/$gifPath"
    fun getImageFolder(englishNameForImages: String) = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$englishNameForImages"
}