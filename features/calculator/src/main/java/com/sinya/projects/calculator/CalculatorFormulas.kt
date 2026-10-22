package com.sinya.projects.calculator

import kotlin.math.log10
import kotlin.math.pow

internal object CalculatorFormulas {
    fun formulaBrzycki(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        val r = vars["r"] ?: 0.0
        return if (r >= 37 || r <= 0) 0.0 else if (r == 1.0) w else w * (36.0 / (37.0 - r))
    }

    fun formulaEpley(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        val r = vars["r"] ?: 0.0
        return if (r <= 0) 0.0 else if (r == 1.0) w else w * (1.0 + r / 30.0)
    }

    fun formulaLander(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        val r = vars["r"] ?: 0.0
        return if (r <= 0 || r >= 38) 0.0 else if (r == 1.0) w else (100.0 * w) / (101.3 - 2.67123 * r)
    }

    fun formula1RmAverage(vars: Map<String, Double>): Double {
        return (formulaBrzycki(vars) + formulaEpley(vars) + formulaLander(vars)) / 3.0
    }

    // --- 2. Процент подкожного жира (Метод ВМС США / US Navy) ---
    fun formulaNavyBodyFatMen(vars: Map<String, Double>): Double {
        val h = vars["h"] ?: 0.0
        val waist = vars["waist"] ?: 0.0
        val neck = vars["neck"] ?: 0.0
        if (h <= 0 || waist - neck <= 0) return 0.0
        val bf = 495.0 / (1.0324 - 0.19077 * log10(waist - neck) + 0.15456 * log10(h)) - 450.0
        return bf.coerceIn(0.0, 70.0)
    }

    fun formulaNavyBodyFatWomen(vars: Map<String, Double>): Double {
        val h = vars["h"] ?: 0.0
        val waist = vars["waist"] ?: 0.0
        val hips = vars["hips"] ?: 0.0
        val neck = vars["neck"] ?: 0.0
        if (h <= 0 || (waist + hips - neck) <= 0) return 0.0
        val bf = 495.0 / (1.29579 - 0.35004 * log10(waist + hips - neck) + 0.22100 * log10(h)) - 450.0
        return bf.coerceIn(0.0, 70.0)
    }

    // --- 3. Индекс безжировой массы тела (FFMI) ---
    fun formulaLeanBodyMass(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        val bf = vars["bf"] ?: 0.0
        if (w <= 0 || bf < 0 || bf >= 100) return 0.0
        return w * (1.0 - (bf / 100.0))
    }

    fun formulaFfmi(vars: Map<String, Double>): Double {
        val hMeters = (vars["h"] ?: 0.0) / 100.0
        if (hMeters <= 0) return 0.0
        val lbm = formulaLeanBodyMass(vars)
        return lbm / hMeters.pow(2)
    }

    // Нормализованный FFMI (скорректированный под средний рост 180 см)
    fun formulaNormalizedFfmi(vars: Map<String, Double>): Double {
        val hMeters = (vars["h"] ?: 0.0) / 100.0
        if (hMeters <= 0) return 0.0
        val ffmi = formulaFfmi(vars)
        return if (ffmi > 0) ffmi + 6.1 * (1.8 - hMeters) else 0.0
    }

    // --- 4. Конвертер RPE / RIR в рабочие веса ---
    // Возвращает процент от 1ПМ с учетом целевых повторений и запаса (RIR)
    fun formulaRirPercentage(vars: Map<String, Double>): Double {
        val reps = vars["r"] ?: 0.0
        val rir = vars["rir"] ?: 0.0
        if (reps <= 0 || rir < 0) return 0.0
        val effectiveReps = reps + rir
        if (effectiveReps >= 37) return 0.0
        val pct = if (effectiveReps == 1.0) 100.0 else ((37.0 - effectiveReps) / 36.0) * 100.0
        return pct.coerceIn(0.0, 100.0)
    }

    // Возвращает рекомендуемый рабочий вес на штанге (в кг)
    fun formulaRirWorkingWeight(vars: Map<String, Double>): Double {
        val oneRm = vars["one_rm"] ?: 0.0
        if (oneRm <= 0) return 0.0
        return oneRm * (formulaRirPercentage(vars) / 100.0)
    }

    // Показывает соответствующий показатель RPE (10 - RIR)
    fun formulaEquivalentRpe(vars: Map<String, Double>): Double {
        val rir = vars["rir"] ?: 0.0
        return (10.0 - rir).coerceIn(1.0, 10.0)
    }

    // --- 5. Калькулятор суточной нормы воды (по нормам FPA) ---
    // Базовая норма в покое: 35 мл на 1 кг веса (в литрах)
    fun formulaWaterBase(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        return if (w <= 0) 0.0 else (w * 35.0) / 1000.0
    }

    // Дополнительная вода на тренировку: ~600 мл на каждый час нагрузки (в литрах)
    fun formulaWaterWorkoutAdd(vars: Map<String, Double>): Double {
        val durationMin = vars["t"] ?: 0.0
        return if (durationMin <= 0) 0.0 else (durationMin / 60.0) * 0.6
    }

    // Итого в день тренировки (умеренный климат)
    fun formulaWaterTotalNormal(vars: Map<String, Double>): Double {
        val base = formulaWaterBase(vars)
        return if (base <= 0) 0.0 else base + formulaWaterWorkoutAdd(vars)
    }

    // Итого в день тренировки (жаркий климат / высокая потливость: 40 мл/кг + 800 мл/ч)
    fun formulaWaterTotalHot(vars: Map<String, Double>): Double {
        val w = vars["w"] ?: 0.0
        val durationMin = vars["t"] ?: 0.0
        if (w <= 0) return 0.0
        val baseHot = (w * 40.0) / 1000.0
        val workoutHot = if (durationMin <= 0) 0.0 else (durationMin / 60.0) * 0.8
        return baseHot + workoutHot
    }
}