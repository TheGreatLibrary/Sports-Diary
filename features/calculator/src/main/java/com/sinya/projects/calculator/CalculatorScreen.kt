package com.sinya.projects.calculator

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sinya.projects.calculator.components.InputCalculateCard
import com.sinya.projects.calculator.components.OutputCalculateCard
import com.sinya.projects.domain.model.CalculatorConfig
import com.sinya.projects.domain.model.ProportionRow
import com.sinya.projects.domain.model.ResultSpec
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.model.VariableSpec
import com.sinya.projects.ui.R.string
import com.sinya.projects.ui.components.dropdown.DropdownCheckboxMenu
import com.sinya.projects.ui.components.dropdown.components.DropdownElement
import com.sinya.projects.ui.components.screen.ScreenColumn
import java.util.Locale

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun CalculatorScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val calculators = remember {
        listOf(
            CalculatorConfig(
                id = "1rm",
                titleRes = R.string.calc_1rm_title,
                descriptionRes = R.string.calc_1rm_desc,
                variables = listOf(
                    VariableSpec(
                        key = "w",
                        titleRes = R.string.lifted_weight,
                        measureRes = string.kg,
                        defaultValue = "80"
                    ),
                    VariableSpec(
                        key = "r",
                        titleRes = R.string.repetitions,
                        measureRes = string.count,
                        defaultValue = "8"
                    )
                ),
                results = listOf(
                    ResultSpec(R.string.brzycki, string.kg, CalculatorFormulas::formulaBrzycki),
                    ResultSpec(R.string.epley, string.kg, CalculatorFormulas::formulaEpley),
                    ResultSpec(R.string.lander, string.kg, CalculatorFormulas::formulaLander),
                    ResultSpec(
                        R.string.calc_avg_value,
                        string.kg,
                        CalculatorFormulas::formula1RmAverage
                    )
                )
            ),

            // 2. Процент жира (Метод ВМС США)
            CalculatorConfig(
                id = "navy_bf",
                titleRes = R.string.calc_bf_title,
                descriptionRes = R.string.calc_bf_desc,
                variables = listOf(
                    VariableSpec(key = "h", titleRes = R.string.calc_var_height, measureRes = string.cm, defaultValue = "178"),
                    VariableSpec(key = "neck", titleRes = R.string.calc_var_neck, measureRes = string.cm, defaultValue = "38"),
                    VariableSpec(key = "waist", titleRes = R.string.calc_var_waist, measureRes = string.cm, defaultValue = "82"),
                    VariableSpec(key = "hips", titleRes = R.string.calc_var_hips, measureRes = string.cm, defaultValue = "95")
                ),
                results = listOf(
                    ResultSpec(R.string.calc_res_bf_men, R.string.unit_percent, CalculatorFormulas::formulaNavyBodyFatMen),
                    ResultSpec(R.string.calc_res_bf_women, R.string.unit_percent, CalculatorFormulas::formulaNavyBodyFatWomen)
                )
            ),

            // 3. Индекс безжировой массы (FFMI)
            CalculatorConfig(
                id = "ffmi",
                titleRes = R.string.calc_ffmi_title,
                descriptionRes = R.string.calc_ffmi_desc,
                variables = listOf(
                    VariableSpec(key = "w", titleRes = R.string.calc_var_body_weight, measureRes = string.kg, defaultValue = "75"),
                    VariableSpec(key = "h", titleRes = R.string.calc_var_height, measureRes = string.cm, defaultValue = "178"),
                    VariableSpec(key = "bf", titleRes = R.string.calc_var_bf_percent, measureRes = R.string.unit_percent, defaultValue = "15")
                ),
                results = listOf(
                    ResultSpec(R.string.calc_res_lbm, string.kg, CalculatorFormulas::formulaLeanBodyMass),
                    ResultSpec(R.string.calc_res_ffmi_base, R.string.unit_index, CalculatorFormulas::formulaFfmi),
                    ResultSpec(R.string.calc_res_ffmi_norm, R.string.unit_index, CalculatorFormulas::formulaNormalizedFfmi)
                )
            ),

            // 4. Рабочий вес по RPE / RIR
            CalculatorConfig(
                id = "rpe_rir",
                titleRes = R.string.calc_rpe_title,
                descriptionRes = R.string.calc_rpe_desc,
                variables = listOf(
                    VariableSpec(key = "one_rm", titleRes = R.string.calc_var_1rm, measureRes = string.kg, defaultValue = "100"),
                    VariableSpec(key = "r", titleRes = R.string.repetitions, measureRes = string.count, defaultValue = "8"),
                    VariableSpec(key = "rir", titleRes = R.string.calc_var_rir, measureRes = string.count, defaultValue = "2")
                ),
                results = listOf(
                    ResultSpec(R.string.calc_res_working_weight, string.kg, CalculatorFormulas::formulaRirWorkingWeight),
                    ResultSpec(R.string.calc_res_1rm_percent, R.string.unit_percent, CalculatorFormulas::formulaRirPercentage),
                    ResultSpec(R.string.calc_res_rpe_equiv, R.string.unit_rpe, CalculatorFormulas::formulaEquivalentRpe)
                )
            ),

            // 5. Норма воды в день (FPA)
            CalculatorConfig(
                id = "water",
                titleRes = R.string.calc_water_title,
                descriptionRes = R.string.calc_water_desc,
                variables = listOf(
                    VariableSpec(key = "w", titleRes = R.string.calc_var_body_weight, measureRes = string.kg, defaultValue = "75"),
                    VariableSpec(key = "t", titleRes = R.string.calc_var_workout_time, measureRes = R.string.unit_min, defaultValue = "60")
                ),
                results = listOf(
                    ResultSpec(R.string.calc_res_water_base, R.string.unit_liters, CalculatorFormulas::formulaWaterBase),
                    ResultSpec(R.string.calc_res_water_during, R.string.unit_liters, CalculatorFormulas::formulaWaterWorkoutAdd),
                    ResultSpec(R.string.calc_res_water_normal, R.string.unit_liters, CalculatorFormulas::formulaWaterTotalNormal),
                    ResultSpec(R.string.calc_res_water_hot, R.string.unit_liters, CalculatorFormulas::formulaWaterTotalHot)
                )
            )
        )
    }
    var selectedCalc by remember { mutableStateOf(calculators.first()) }
    var inputValues by remember(selectedCalc) {
        mutableStateOf(selectedCalc.variables.associate { it.key to it.defaultValue })
    }
    val parsedVariables: Map<String, Double> = remember(inputValues) {
        inputValues.mapValues { (_, textValue) ->
            textValue.replace(',', '.').toDoubleOrNull() ?: 0.0
        }
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Box {
        ScreenColumn(
            arrangement = Arrangement.spacedBy(12.dp),
            navigationType = TypeTopNavigation.WithoutIcon(
                onBackClick = onBackClick,
                title = stringResource(string.calculate_title),
            )
        ) {
            DropdownCheckboxMenu(
                items = calculators,
                title = stringResource(R.string.calculator_type),
                onInfoClick = null,
                alignment = Alignment.Start,
                selectedItems = listOf(selectedCalc),
                expanded = dropdownExpanded,
                onExpanded = { dropdownExpanded = it },
                nameObject = { list -> list.joinToString { context.getString(it.titleRes) } },
                onPlusClick = null
            ) { item ->
                DropdownElement(
                    title = stringResource(item.titleRes),
                    onClick = {
                        selectedCalc = item
                        dropdownExpanded = false
                    }
                )
            }

            selectedCalc.variables.forEachIndexed { index, variable ->
                val currentValue = inputValues[variable.key] ?: "0"

                InputCalculateCard(
                    title = stringResource(variable.titleRes),
                    item = ProportionRow(
                        id = index,
                        title = context.getString(variable.titleRes),
                        value = currentValue,
                        unitMeasure = context.getString(variable.measureRes),
                        delta = null
                    ),
                    onValueChange = { _, _, newValue ->
                        inputValues = inputValues.toMutableMap().apply {
                            put(variable.key, newValue)
                        }
                    },
                    context = context,
                )
            }

            Text(
                text = stringResource(R.string.result),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Start,
            )

            selectedCalc.results.forEachIndexed { index, resultItem ->
                val rawResult = resultItem.formula(parsedVariables)
                val formattedResult = String.format(Locale.US, "%.1f", rawResult)

                OutputCalculateCard(
                    title = stringResource(resultItem.titleRes),
                    item = ProportionRow(
                        id = index + 100,
                        title = context.getString(resultItem.titleRes),
                        value = formattedResult,
                        unitMeasure = context.getString(resultItem.measureRes),
                        delta = null
                    ),
                    onValueChange = { _, _, _ -> },
                    context = context,
                )
            }
        }
    }
}