package com.sinya.projects.sportsdiary.ui.features.chart.utils

import android.annotation.SuppressLint
import com.sinya.projects.sportsdiary.core.domain.enums.TypeTime
import com.sinya.projects.sportsdiary.ui.features.chart.model.AxisData
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartLine
import com.sinya.projects.sportsdiary.ui.features.chart.model.ChartPoint
import com.sinya.projects.sportsdiary.ui.features.chart.model.IntScale
import com.sinya.projects.sportsdiary.ui.features.chart.model.groupPointsByTimeMode
import com.sinya.projects.sportsdiary.ui.features.chart.model.parseDateByMode
import kotlin.math.ceil
import kotlin.math.roundToInt

object ChartDataUtils {

    fun createAxisDataWithDate(lines: List<ChartLine>, timeMode: TypeTime): AxisData {
        val unifiedXAxis = lines.firstOrNull()?.points?.parseDateByMode(timeMode) ?: emptyList()
        val mainUnifiedXAxis = lines.firstOrNull()?.points?.groupPointsByTimeMode(timeMode) ?: emptyList()

        val yRange = calculateYRange(lines)
        val yMin = calculateYMin(lines)
        val maxValue = lines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.maxOrNull() ?: 0f
        val yLabels = createYLabels(yMin, yRange, 5, maxValue)


        return AxisData(
            unifiedXAxis = unifiedXAxis,
            mainUnifiedXAxis = mainUnifiedXAxis,
            xDateToIndex = unifiedXAxis.withIndex().associate { (i, d) -> d to i },
            normalizedLines = lines.mapIndexed { index, line ->
                line.copy(
                    lineColor = ChartColors.getColor(index, lines.size)
                )
            },
            yRange = yRange,
            yMin = yMin,
            yMax = maxValue,
            yLabels = yLabels
        )
    }

    fun createAxisData(lines: List<ChartLine>): AxisData {
        val unifiedXAxis = createUnifiedXAxis(lines)
        val normalizedLines = normalizeLines(lines, unifiedXAxis)
        val yRange = calculateYRange(normalizedLines)
        val yMin = calculateYMin(normalizedLines)
        val maxValue = normalizedLines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.maxOrNull() ?: 0f
        val yLabels = createYLabels(yMin, yRange, 5, maxValue)

        return AxisData(
            unifiedXAxis = unifiedXAxis,
            xDateToIndex = unifiedXAxis.withIndex().associate { (i, d) -> d to i },
            normalizedLines = normalizedLines.mapIndexed { index, line ->
                line.copy(
                    lineColor = ChartColors.getColor(index, lines.size)
                )
            },
            yRange = yRange,
            yMin = yMin,
            yMax = maxValue,
            yLabels = yLabels,
        )
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

    fun calculateYRange(normalizedLines: List<ChartLine>): Float {
        val allYValues = normalizedLines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }
        val minY = allYValues.minOrNull() ?: 0f
        val maxY = allYValues.maxOrNull() ?: 1f
        val range = maxY - minY
        return range.takeIf { it != 0f } ?: 1f
    }

    fun calculateYMin(normalizedLines: List<ChartLine>): Float {
        return normalizedLines.flatMap { line ->
            line.points.map { it.yValue }.filter { !it.isNaN() }
        }.minOrNull() ?: 0f
    }

    fun createYLabels(
        yMin: Float,
        yRange: Float,
        yGridLines: Int,
        maxValue: Float
    ): List<String> {
        val formatLabel = getSmartFormat(maxValue)

        return (yGridLines downTo 0).map { i ->
            val v = yMin + (yRange / yGridLines) * i
            formatLabel(v)
        }
    }

    @SuppressLint("DefaultLocale")
    fun getSmartFormat(maxValue: Float): (Float) -> String {
        return when {
            maxValue >= 1000 -> { value -> (value / 1000).let { "${it.roundToInt()}k" } }
            maxValue >= 100 -> { value -> value.roundToInt().toString() }
            maxValue >= 10 -> { value -> String.format("%.1f", value).replace(',', '.') }
            maxValue >= 1 -> { value -> String.format("%.2f", value).replace(',', '.') }
            maxValue >= 0.1 -> { value -> String.format("%.3f", value).replace(',', '.') }
            else -> { value -> String.format("%.4f", value).replace(',', '.') }
        }
    }
}