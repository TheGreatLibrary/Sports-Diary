package com.sinya.projects.domain.utils

object UrlConfig {
    const val CONFIG = "https://raw.githubusercontent.com/TheGreatLibrary/SportDiaryChanges/main/changelog.json"
    const val MARKET_RU_STORE = "https://www.rustore.ru/catalog/app/com.sinya.projects.sportsdiary"
    const val HEALTH_APK = "market://details?id=com.google.android.apps.healthdata"
    const val FORM_YANDEX_FOR_GOOGLE_TEST = "https://forms.yandex.ru/u/6ae066872c4f890183916ccb"

    fun getGif(gifPath: String) = "https://raw.githubusercontent.com/hasaneyldrm/exercises-dataset/main/$gifPath"
    fun getImageFolder(englishNameForImages: String) = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/$englishNameForImages"
}