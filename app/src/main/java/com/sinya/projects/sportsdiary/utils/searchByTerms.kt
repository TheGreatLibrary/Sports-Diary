package com.sinya.projects.sportsdiary.utils

fun <T> List<T>.searchByTerms(
    query: String,
    selector: (T) -> String
): List<T> {
    if (query.isBlank()) return this

    val terms = query.trim().lowercase()
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }

    return this.mapNotNull { exercise ->
        val text = selector(exercise).lowercase()

        var score = 0
        terms.forEach { term ->
            when {
                // Точное совпадение слова - максимальный балл
                text.split(" ").any { it == term } -> score += 3
                // Начало слова - средний балл
                text.split(" ").any { it.startsWith(term) } -> score += 2
                // Просто содержит - минимальный балл
                text.contains(term) -> score += 1
            }
        }

        if (score > 0) exercise to score else null
    }
        .sortedByDescending { it.second }
        .map { it.first }
}