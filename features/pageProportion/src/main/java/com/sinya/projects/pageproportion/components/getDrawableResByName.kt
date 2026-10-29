package com.sinya.projects.pageproportion.components

import com.sinya.projects.pageproportion.R

internal fun getDrawableResByName(name: String?): Int {
    return when (name) {
        "image_biceps" -> R.drawable.image_biceps
        "image_calf" -> R.drawable.image_calf
        "image_chest" -> R.drawable.image_chest
        "image_forearm" -> R.drawable.image_forearm
        "image_height" -> R.drawable.image_height
        "image_hips" -> R.drawable.image_hips
        "image_neck" -> R.drawable.image_neck
        "image_quadriceps" -> R.drawable.image_quadriceps
        "image_shoulder" -> R.drawable.image_shoulder
        "image_shoulder_width" -> R.drawable.image_shoulder_width
        "image_thigh" -> R.drawable.image_thigh
        "image_triceps" -> R.drawable.image_triceps
        "image_waist" -> R.drawable.image_waist
        "image_wrist" -> R.drawable.image_wrist
        else -> com.sinya.projects.ui.R.drawable.train_muscul
    }
}