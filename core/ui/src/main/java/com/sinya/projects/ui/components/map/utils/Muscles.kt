package com.sinya.projects.ui.components.map.utils

import androidx.annotation.DrawableRes
import com.sinya.projects.domain.model.DataExerciseMuscles
import com.sinya.projects.ui.R

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