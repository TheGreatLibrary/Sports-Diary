package com.sinya.projects.sportsdiary.utils

fun <T> List<T>.searchByTerms(
    query: String,
    selector: (T) -> String,
    isCustomSelector: ((T) -> Boolean)? = null
): List<T> {
    if (query.isBlank()) {
        return if (isCustomSelector != null) {
            this.sortedByDescending { isCustomSelector(it) }
        } else {
            this
        }
    }

    val terms = query.trim().lowercase()
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }

    return this.mapNotNull { exercise ->
        val text = selector(exercise).lowercase()

        var score = 0
        terms.forEach { term ->
            when {
                text.split(" ").any { it == term } -> score += 3
                text.split(" ").any { it.startsWith(term) } -> score += 2
                text.contains(term) -> score += 1
            }
        }

        if (score > 0) exercise to score else null
    }
        .sortedWith(
            compareByDescending<Pair<T, Int>> { (exercise, _) ->
                isCustomSelector?.invoke(exercise) ?: false
            }.thenByDescending { (_, score) ->
                score
            }
        )
        .map { it.first }
}