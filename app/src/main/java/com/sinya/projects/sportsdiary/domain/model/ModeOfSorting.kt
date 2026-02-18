package com.sinya.projects.sportsdiary.domain.model

import android.content.Context
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.data.database.entity.Proportions
import com.sinya.projects.sportsdiary.data.database.entity.filterByYearMonth
import com.sinya.projects.sportsdiary.data.database.entity.monthsForYear
import com.sinya.projects.sportsdiary.data.database.entity.years
import com.sinya.projects.sportsdiary.domain.enums.TypeCustom
import com.sinya.projects.sportsdiary.ui.theme.Shapes

sealed class ModeOfSorting(
    val code: Int,
    val radioItem: RadioItem<Int?>
) {
    abstract fun apply(param: SortParam): ModeOfSorting
    abstract fun <T> filter(items: List<T>): List<T>
    abstract fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>>

    // Trainings
    data class Time(
        val year: Int = -1,
        val month: Int = -1
    ) : ModeOfSorting(
        code = 0,
        radioItem = RadioItem(null, R.drawable.train_time)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Year -> copy(year = param.value, month = -1)
                is SortParam.Month -> copy(month = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is Training -> (items as List<Training>).filterByYearMonth(year, month) as List<T>
                is Proportions -> (items as List<Proportions>).filterByYearMonth(
                    year,
                    month
                ) as List<T>

                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)

            val (years, getMonths) = when (items.firstOrNull()) {
                is Training -> {
                    val trainings = items as List<Training>
                    trainings.years to { y: Int -> trainings.monthsForYear(y) }
                }

                is Proportions -> {
                    val proportions = items as List<Proportions>
                    proportions.years to { y: Int -> proportions.monthsForYear(y) }
                }

                else -> emptyList<Int>() to { _: Int -> emptyList() }
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.year,
                    options = (listOf(RadioItem(all, null, -1)) +
                            years.map { RadioItem(it.toString(), null, it) }) as List<RadioItem<M>>,
                    selectedValue = year as M,
                    onSelect = { SortParam.Year(it as Int) },
                    shape = Shapes.extraLarge
                ),
                FilterBuilder(
                    titleRes = R.string.month,
                    options = (listOf(RadioItem(all, null, -1)) +
                            getMonths(year).map {
                                RadioItem(
                                    it.toString(),
                                    null,
                                    it
                                )
                            }) as List<RadioItem<M>>,
                    selectedValue = month as M,
                    onSelect = { SortParam.Month(it as Int) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    data class Muscle(
        val category: String? = ""
    ) : ModeOfSorting(
        code = 1,
        radioItem = RadioItem(null, R.drawable.train_muscul)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Category -> copy(category = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).filterByMuscle(category) as List<T>
                is Training -> (items as List<Training>).filterByMuscle(category) as List<T>
                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is Training -> Pair(
                    (items as List<Training>).categories,
                    R.string.categories
                )
                is ExerciseWithMuscles -> Pair(
                    (items as List<ExerciseWithMuscles>).allMuscles,
                    R.string.ex_muscle
                )
                else -> Pair(
                    emptyList(),
                    R.string.categories
                )
            }

            return listOf(
                FilterBuilder(
                    titleRes = categories.second,
                    options = listOf(RadioItem(all, null, "" as M)) + categories.first.map {
                        RadioItem(
                            if (it.isNullOrEmpty()) noOne else it,
                            null,
                            (if (it.isNullOrEmpty()) "no one" else it) as M
                        )
                    },
                    selectedValue = category as M,
                    onSelect = { SortParam.Category(it as String?) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    //Exercises
    data class Category(
        val category: String? = ""
    ) : ModeOfSorting(
        code = 2,
        radioItem = RadioItem(null, null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Category -> copy(category = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).filterByCategory(
                    category
                ) as List<T>

                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).categories.map {
                    RadioItem(
                        if (it.isNullOrEmpty()) noOne else it,
                        null,
                        (if (it.isNullOrEmpty()) "no one" else it) as M
                    )
                }

                else -> emptyList()
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.categories,
                    options = listOf(RadioItem(all, null, "" as M)) + categories,
                    selectedValue = category as M,
                    onSelect = { SortParam.Category(it as String?) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    data class Equipment(
        val equipment: String? = ""
    ) : ModeOfSorting(
        code = 3,
        radioItem = RadioItem(null, null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Equipment -> copy(equipment = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).filterByEquipment(
                    equipment
                ) as List<T>

                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).equipment.map {
                    RadioItem(
                        if (it.isNullOrEmpty()) noOne else it,
                        null,
                        (if (it.isNullOrEmpty()) "no one" else it) as M
                    )
                }

                else -> emptyList()
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.ex_equipment,
                    options = listOf(RadioItem(all, null, "" as M)) + categories,
                    selectedValue = equipment as M,
                    onSelect = { SortParam.Equipment(it as String?) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    data class Level(val level: String? = "") : ModeOfSorting(
        code = 4,
        radioItem = RadioItem(null, null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Level -> copy(level = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).filterByLevel(level) as List<T>
                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val all = context.getString(R.string.all)
            val noOne = context.getString(R.string.no_one)

            val categories = when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).level.map {
                    RadioItem(
                        if (it.isNullOrEmpty()) noOne else it,
                        null,
                        (if (it.isNullOrEmpty()) "no one" else it) as M
                    )
                }

                else -> emptyList()
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.ex_level,
                    options = listOf(RadioItem(all, null, "" as M)) + categories,
                    selectedValue = level as M,
                    onSelect = { SortParam.Level(it as String?) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    data class Custom(val custom: TypeCustom = TypeCustom.ALL) : ModeOfSorting(
        code = 5,
        radioItem = RadioItem(null)
    ) {
        override fun apply(param: SortParam): ModeOfSorting =
            when (param) {
                is SortParam.Custom -> copy(custom = param.value)
                else -> this
            }

        @Suppress("UNCHECKED_CAST")
        override fun <T> filter(items: List<T>): List<T> {
            return when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).filterByCustom(custom) as List<T>
                else -> items
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T, M> categories(items: List<T>, context: Context): List<FilterBuilder<M>> {
            val categories = when (items.firstOrNull()) {
                is ExerciseWithMuscles -> (items as List<ExerciseWithMuscles>).custom.map {
                    val item = TypeCustom.getType(it)
                    RadioItem(context.getString(item.stringRes), null, item as M)
                }

                else -> emptyList()
            }

            return listOf(
                FilterBuilder(
                    titleRes = R.string.ex_level,
                    options = listOf(RadioItem(context.getString(TypeCustom.ALL.stringRes), null, TypeCustom.ALL as M)) + categories,
                    selectedValue = custom as M,
                    onSelect = { SortParam.Custom(it as TypeCustom) },
                    shape = Shapes.extraLarge
                )
            )
        }
    }

    companion object {
        fun fromCode(code: Int?): ModeOfSorting =
            when (code) {
                1 -> Muscle("")
                2 -> Category("")
                3 -> Equipment("")
                4 -> Level("")
                else -> Time()
            }

        fun trainingsModes(): List<RadioItem<Int?>> =
            listOf(
                Time().radioItem,
                Muscle("").radioItem
            ).mapIndexed { i, it ->
                it.copy(value = i)
            }

        fun exercisesModes(): List<RadioItem<Int?>> =
            listOf(
                Level(""),
                Category(""),
                Muscle(""),
                Equipment(""),
                Custom()
            ).map {
                it.radioItem.copy(value = it.code)
            }
    }
}