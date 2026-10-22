package com.sinya.projects.ui.components.chart.utils

import android.annotation.SuppressLint
import com.sinya.projects.domain.enums.TypeTime
import com.sinya.projects.domain.model.ChartLine
import com.sinya.projects.domain.model.ChartPoint
import com.sinya.projects.domain.model.groupPointsByTimeMode
import com.sinya.projects.domain.model.parseDateByMode
import com.sinya.projects.ui.components.chart.model.AxisData
import com.sinya.projects.ui.components.chart.model.IntScale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object ChartDataUtils {

    fun createAxisDataWithDate(lines: List<ChartLine>, timeMode: TypeTime): AxisData {
        val unifiedXAxis = lines.firstOrNull()?.points?.parseDateByMode(timeMode) ?: emptyList()
        val mainUnifiedXAxis = lines.firstOrNull()?.points?.groupPointsByTimeMode(timeMode) ?: emptyList()

        val yMin = calculateYMin(lines)
        val maxValue = lines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.maxOrNull() ?: 0f

        val step = niceStep((maxValue - yMin) / 4f)
        val niceMin = floor(yMin / step) * step
        val niceMax = ceil(maxValue / step) * step
        val yRange = (niceMax - niceMin).takeIf { it > 0f } ?: 1f
        val yLabels = createYLabels(niceMin, niceMax, 5)

        return AxisData(
            unifiedXAxis = unifiedXAxis,
            mainUnifiedXAxis = mainUnifiedXAxis,
            xDateToIndex = unifiedXAxis.withIndex().associate { (i, d) -> d to i },
            normalizedLines = lines.mapIndexed { index, line ->
                line.copy(lineColor = ChartColors.getColor(index, lines.size))
            },
            yRange = yRange,
            yMin = niceMin,
            yMax = niceMax,
            yLabels = yLabels
        )
    }

    fun createAxisData(lines: List<ChartLine>): AxisData {
        val unifiedXAxis = createUnifiedXAxis(lines)
        val normalizedLines = normalizeLines(lines, unifiedXAxis)
        val yMin = calculateYMin(normalizedLines)
        val maxValue = normalizedLines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.maxOrNull() ?: 0f

        // Пересчитываем yRange и yMax по «красивому» шагу
        val labels = createYLabels(yMin, maxValue, 5)
        val step = niceStep((maxValue - yMin) / 4f)
        val niceMin = floor(yMin / step) * step
        val niceMax = ceil(maxValue / step) * step
        val yRange = (niceMax - niceMin).takeIf { it > 0f } ?: 1f

        return AxisData(
            unifiedXAxis = unifiedXAxis,
            xDateToIndex = unifiedXAxis.withIndex().associate { (i, d) -> d to i },
            normalizedLines = normalizedLines.mapIndexed { index, line ->
                line.copy(lineColor = ChartColors.getColor(index, lines.size))
            },
            yRange = yRange,
            yMin = niceMin,
            yMax = niceMax,
            yLabels = labels,
        )
    }
    fun createYLabels(
        yMin: Float,
        yMax: Float,
        targetCount: Int = 5
    ): List<String> {
        // Защита: одна точка или плоский график
        if (yMax.isNaN() || yMin.isNaN() || yMax <= yMin) {
            val fmt = getSmartFormat(abs(yMin).takeIf { it > 0f } ?: 1f)
            return listOf(fmt(yMin))
        }

        val rawStep = (yMax - yMin) / (targetCount - 1).coerceAtLeast(1)
        val step = niceStep(rawStep)
        val fmt = getSmartFormat(step)

        // Округляем yMin вниз до кратного step, чтобы подписи были ровные
        val start = floor(yMin / step) * step
        val end = ceil(yMax / step) * step

        val labels = mutableListOf<String>()
        var v = start
        // защита от бесконечного цикла
        var guard = 0
        while (v <= end + step * 0.5f && guard < 50) {
            labels.add(fmt(v))
            v += step
            guard++
        }
        return labels.asReversed()
    }

    fun niceStep(rawStep: Float): Float {
        if (rawStep.isNaN() || rawStep <= 0f) return 1f
        val exp = floor(log10(rawStep.toDouble())).toInt()
        val base = 10.0.pow(exp).toFloat()
        val frac = rawStep / base
        val niceFrac = when {
            frac <= 1f -> 1f
            frac <= 2f -> 2f
            frac <= 5f -> 5f
            else -> 10f
        }
        return niceFrac * base
    }

    fun buildIntScale(maxValue: Float, maxLines: Int = 5): IntScale {
        val maxInt = ceil(maxValue).toInt().coerceAtLeast(1)
        val step = ceil(maxInt / maxLines.toFloat()).toInt().coerceAtLeast(1)
        val lines = ceil(maxInt / step.toFloat()).toInt()

        return IntScale(
            min = 0,
            max = lines * step,
            step = step,
            lines = lines
        )
    }

    fun createUnifiedXAxis(lines: List<ChartLine>): List<String> {
        return lines.flatMap { line ->
            line.points.map { it.xDate }
        }.distinct().sortedBy { date ->
            lines.flatMap { it.points }
                .firstOrNull { it.xDate == date }?.date ?: date
        }
    }

    fun normalizeLines(
        lines: List<ChartLine>,
        unifiedXAxis: List<String>
    ): List<ChartLine> {
        return lines.map { line ->
            // Создаем отображение дата -> точка для быстрого поиска
            val pointMap = line.points.associateBy { it.xDate }

            // Для каждой даты в унифицированной оси создаем точку
            val normalizedPoints = unifiedXAxis.mapIndexed { index, xDate ->
                val existingPoint = pointMap[xDate]
                if (existingPoint != null) {
                    // Точка существует - используем её значение, но обновляем id
                    existingPoint.copy(id = index)
                } else {
                    // Точки нет - создаем с NaN (разрыв в линии)
                    ChartPoint(
                        id = index,
                        xDate = xDate,
                        yValue = Float.NaN,
                        date = "" // Дата не важна для отсутствующих точек
                    )
                }
            }

            line.copy(points = normalizedPoints)
        }

    }

    fun calculateYMin(normalizedLines: List<ChartLine>): Float {
        return normalizedLines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.minOrNull() ?: 0f
    }

    @SuppressLint("DefaultLocale")
    fun getSmartFormat(step: Float): (Float) -> String {
        val absStep = abs(step).takeIf { it > 0f && !it.isNaN() } ?: 1f
        return when {
            absStep >= 1000 -> { v ->
                val k = v / 1000f
                if (abs(k - k.roundToInt()) < 0.05f) "${k.roundToInt()}k"
                else String.format("%.1f", k).replace(',', '.') + "k"
            }
            absStep >= 100 -> { v -> String.format("%.0f", v).replace(',', '.') }
            absStep >= 10 -> { v -> String.format("%.0f", v).replace(',', '.') }
            absStep >= 1 -> { v -> String.format("%.0f", v).replace(',', '.') }
            absStep >= 0.1f -> { v -> String.format("%.1f", v).replace(',', '.') }
            absStep >= 0.01f -> { v -> String.format("%.2f", v).replace(',', '.') }
            else -> { v -> String.format("%.3f", v).replace(',', '.') }
        }
    }
}