package com.sinya.projects.sportsdiary.core.domain.enums

import androidx.annotation.DrawableRes
import com.sinya.projects.sportsdiary.R
import com.sinya.projects.sportsdiary.ui.features.map.model.DataExerciseMuscles


//enum class Muscles(val id: Int, @DrawableRes val res: Int, val stateForward: Boolean = true) {
//    ChestHigh(1, R.drawable.body_chest_high), // верхняя грудь
//    ChestMiddle(2, R.drawable.body_chest_middle), // средняя грудь
//    ChestLow(3, R.drawable.body_chest_low), // нижняя грудь
//    ShouldersForward(4, R.drawable.body_shoulders_forward), // передняя дельта
//    ShouldersMiddle(5, R.drawable.body_shoulders_middle), // средняя дельта
//    ShouldersMiddleBack(5, R.drawable.body_shoulders_middle_back, false), // средняя дельта
//    ShouldersBack(6, R.drawable.body_shoulders_back, false), // задняя дельта
//    TricepsLong(7, R.drawable.body_triceps_long, false), // длинный трицепс
//    TricepsLateral(8, R.drawable.body_triceps_lateral, false), // латеральный трицепс
//    TricepsMedial(9, R.drawable.body_triceps_medial, false), // медиальный трицепс
//    ForearmExtensors(10, R.drawable.body_forearm_extensors), // предплечья разгибатели
//    ForearmFlexors(11, R.drawable.body_forearm_flexors, false), // предплечья сгибатели
//    Abs(12, R.drawable.body_press), // пресс
//    AbsBottom(13, R.drawable.body_press_bottom), // нижний пресс
//    AbsDiagonal(14, R.drawable.body_press_diagonal), // косые мышцы пресса
//    AbsDiagonalBack(14, R.drawable.body_press_diagonal_back, false), // косые сзади
//    Jagged(15, R.drawable.body_jagged), // зубчатые
//    Tibial(16, R.drawable.body_tibial), // берцовые
//    Calf(17, R.drawable.body_calf), // икры
//    CalfBack(17, R.drawable.body_calf_back, false), // икры сзади
//    Flounders(18, R.drawable.body_flounders), // камбаловидные
//    FloundersBack(18, R.drawable.body_flounders_back, false), // камбаловидные
//
//    BackHigh(19, R.drawable.body_trapezoid, false), // верх спины
//    BackMiddle(20, R.drawable.body_back_middle, false), // середина спины
//    BackLow(21, R.drawable.body_widest_back, false), // низ спины (поясница, разгибатели)
//
//    WidestHigh(22, R.drawable.body_widest), // верх широчайших
//    WidestHighBack(22, R.drawable.body_widest_back, false), // верх широчайших
//    WidestLow(23, R.drawable.body_widest), // низ широчайших
//    WidestLowBack(23, R.drawable.body_widest_back, false), // низ широчайших
//
//    TrapHigh(24, R.drawable.body_trap_high, false), // верх трапеции
//    TrapMiddle(25, R.drawable.body_trap_middle, false), // середина трапеция
//    TrapLow(26, R.drawable.body_trap_low, false), // низ трапеции
//    Biceps(27, R.drawable.body_biceps), // бицепс
//    Hips(28, R.drawable.body_hips, false), // бедра
//    Quadriceps(29, R.drawable.body_quadriceps), // квадрицепс
//    Adductor(30, R.drawable.body_adductor),
//    AdductorBack(30, R.drawable.body_adductor_back, false),
//    Abductor(31, R.drawable.body_abductor),
//    AbductorBack(31, R.drawable.body_abductor_back, false),
//    Buttocks(32, R.drawable.body_buttocks, false), // ягодицы
//    Neck(33, R.drawable.body_neck), // шея
//    Rhomboids(34, R.drawable.body_trap_middle, false), // ромбовидные
//    ErectorSpina(35, R.drawable.body_erector_spina, false); // разгибатели спин
//
//    companion object {
//
//        fun getData(
//            list: List<DataExerciseMuscles>,
//            state: Boolean
//        ): List<DataExerciseMuscles> {
//
//            val exerciseById = list.associateBy { it.muscleId }
//
//            return entries
//                .filter { it.stateForward == state }
//                .map { muscle ->
//                    val exercise = exerciseById[muscle.id]
//
//                    DataExerciseMuscles(
//                        muscleId = muscle.id,
//                        name = exercise?.name ?: muscle.name,
//                        value = exercise?.value ?: 0f,
//                        res = muscle.res,
//                        level = exercise?.level ?: 0,
//                        centerX = muscle.centerX,
//                        centerY = muscle.centerY,
//                        radiusScale = muscle.radiusScale
//                    )
//                }
//        }
//    }
//}


enum class Muscles(
    val id: Int,
    @DrawableRes val res: Int,
    val stateForward: Boolean = true,
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val radiusScale: Float = 0.15f
) {
    ChestHigh(1, R.drawable.body_chest_high, centerX = 0.5f, centerY = 0.22f, radiusScale = 0.12f),
    ChestMiddle(2, R.drawable.body_chest_middle, centerX = 0.5f, centerY = 0.25f, radiusScale = 0.15f),
    ChestLow(3, R.drawable.body_chest_low, centerX = 0.5f, centerY = 0.28f, radiusScale = 0.12f),
    ShouldersForward(4, R.drawable.body_shoulders_forward),
    ShouldersMiddle(5, R.drawable.body_shoulders_middle),
    ShouldersMiddleBack(5, R.drawable.body_shoulders_middle_back, false),
    ShouldersBack(6, R.drawable.body_shoulders_back, false),
    TricepsLong(7, R.drawable.body_triceps_long, false),
    TricepsLateral(8, R.drawable.body_triceps_lateral, false),
    TricepsMedial(9, R.drawable.body_triceps_medial, false),
    ForearmExtensors(10, R.drawable.body_forearm_extensors),
    ForearmFlexors(11, R.drawable.body_forearm_flexors, false),
    Abs(12, R.drawable.body_press, centerX = 0.5f, centerY = 0.38f, radiusScale = 0.18f),
    AbsBottom(13, R.drawable.body_press_bottom, centerX = 0.5f, centerY = 0.44f, radiusScale = 0.12f),
    AbsDiagonal(14, R.drawable.body_press_diagonal),
    AbsDiagonalBack(14, R.drawable.body_press_diagonal_back, false),
    Jagged(15, R.drawable.body_jagged),
    Tibial(16, R.drawable.body_tibial),
    Calf(17, R.drawable.body_calf, centerX = 0.5f, centerY = 0.85f, radiusScale = 0.15f),
    CalfBack(17, R.drawable.body_calf_back, false),
    Flounders(18, R.drawable.body_flounders),
    FloundersBack(18, R.drawable.body_flounders_back, false),

    BackHigh(19, R.drawable.body_trapezoid, false),
    BackMiddle(20, R.drawable.body_back_middle, false),
    BackLow(21, R.drawable.body_widest_back, false),

    WidestHigh(22, R.drawable.body_widest),
    WidestHighBack(22, R.drawable.body_widest_back, false),
    WidestLow(23, R.drawable.body_widest),
    WidestLowBack(23, R.drawable.body_widest_back, false),

    TrapHigh(24, R.drawable.body_trap_high, false),
    TrapMiddle(25, R.drawable.body_trap_middle, false),
    TrapLow(26, R.drawable.body_trap_low, false),
    Biceps(27, R.drawable.body_biceps, centerX = 0.32f, centerY = 0.28f, radiusScale = 0.1f),
    Hips(28, R.drawable.body_hips, false),
    Quadriceps(29, R.drawable.body_quadriceps, centerX = 0.5f, centerY = 0.62f, radiusScale = 0.2f),
    Adductor(30, R.drawable.body_adductor),
    AdductorBack(30, R.drawable.body_adductor_back, false),
    Abductor(31, R.drawable.body_abductor),
    AbductorBack(31, R.drawable.body_abductor_back, false),
    Buttocks(32, R.drawable.body_buttocks, false),
    Neck(33, R.drawable.body_neck),
    Rhomboids(34, R.drawable.body_trap_middle, false),
    ErectorSpina(35, R.drawable.body_erector_spina, false);

    companion object {
        fun getData(
            list: List<DataExerciseMuscles>,
            state: Boolean
        ): List<DataExerciseMuscles> {
            val exerciseById = list.associateBy { it.muscleId }

            return entries
                .filter { it.stateForward == state }
                .map { muscle ->
                    val exercise = exerciseById[muscle.id]

                    DataExerciseMuscles(
                        muscleId = muscle.id,
                        name = exercise?.name ?: muscle.name,
                        value = exercise?.value ?: 0f,
                        res = muscle.res,
                        level = exercise?.level ?: 0,
                        centerX = muscle.centerX,
                        centerY = muscle.centerY,
                        radiusScale = muscle.radiusScale
                    )
                }
        }
    }
}