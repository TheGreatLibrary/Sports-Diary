package com.sinya.projects.sportsdiary.presentation.exerciseEdit

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseMuscles
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.ExerciseTranslations
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.Exercises
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseDialogContent
import com.sinya.projects.sportsdiary.core.domain.model.ExerciseEditItem
import com.sinya.projects.sportsdiary.core.domain.useCase.CheckNameExerciseExistsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetCategoriesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetEquipmentsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseDescriptionUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetExerciseMusclesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetForcesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetLevelsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMechanicsUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMusclesUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.UpsertCustomExerciseUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExerciseEditViewModel.Factory::class)
class ExerciseEditViewModel @AssistedInject constructor(
    @Assisted("id") val id: Int?,
    private val getExerciseDescriptionUseCase: GetExerciseDescriptionUseCase,
    private val getExerciseMusclesUseCase: GetExerciseMusclesUseCase,
    private val upsertCustomExerciseUseCase: UpsertCustomExerciseUseCase,
    private val getForcesUseCase: GetForcesUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getMechanicUseCase: GetMechanicsUseCase,
    private val getEquipmentsUseCase: GetEquipmentsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMusclesUseCase: GetMusclesUseCase,
    private val checkNameExerciseExistsUseCase: CheckNameExerciseExistsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ExerciseEditUiState>(ExerciseEditUiState.Loading)
    val state: StateFlow<ExerciseEditUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): ExerciseEditViewModel
    }

    init {
        loadEditForm()
    }

    fun onEvent(event: ExerciseEditEvent) {
        when (event) {
            is ExerciseEditEvent.OpenBottomSheetTraining -> saveExercise {}

            is ExerciseEditEvent.OnNameChange -> updateIfForm {
                it.copy(
                    exercise = it.exercise.copy(
                        name = event.s,
                        isError = false
                    )
                )
            }

            is ExerciseEditEvent.OnDescriptionChange -> updateIfForm {
                it.copy(exercise = it.exercise.copy(description = event.s))
            }

            is ExerciseEditEvent.OnRuleChange -> updateIfForm {
                it.copy(exercise = it.exercise.copy(rule = event.s))
            }

            is ExerciseEditEvent.MuscleToggle -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    muscles = exerciseForm.muscles.map {
                        if (it.muscleId == event.id) {
                            it.copy(
                                checked = !it.checked,
                                value = if (!it.checked) 2 else 0
                            )
                        } else it
                    }
                )
            }

            ExerciseEditEvent.Save -> saveExercise { id ->
                _state.value = ExerciseEditUiState.Success(id)
            }

            is ExerciseEditEvent.Error -> _state.value = ExerciseEditUiState.Error(event.message)

            is ExerciseEditEvent.OnSelectedCategory -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    exercise = exerciseForm.exercise.copy(category = event.item)
                )
            }

            is ExerciseEditEvent.OnSelectedForce -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    exercise = exerciseForm.exercise.copy(force = event.item)
                )
            }

            is ExerciseEditEvent.OnSelectedMechanic -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    exercise = exerciseForm.exercise.copy(mechanic = event.item)
                )
            }

            is ExerciseEditEvent.OnSelectedEquipment -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    exercise = exerciseForm.exercise.copy(equipment = event.item)
                )
            }

            is ExerciseEditEvent.OnSelectedLevel -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    exercise = exerciseForm.exercise.copy(level = event.item)
                )
            }

            is ExerciseEditEvent.OpenDialogGuide -> openDialogGuide(event.title, event.descr)

            ExerciseEditEvent.DialogShown -> updateIfForm { it.copy(dialogContent = null) }

            is ExerciseEditEvent.MuscleToggleValue -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    muscles = exerciseForm.muscles.map {
                        if (it.muscleId == event.id) it.copy(
                            value = event.value
                        ) else it
                    }
                )
            }

            is ExerciseEditEvent.DeleteMuscle -> updateIfForm { exerciseForm ->
                exerciseForm.copy(
                    muscles = exerciseForm.muscles.map {
                        if (it.muscleId == event.id) it.copy(
                            checked = false,
                            value = 0
                        )
                        else it
                    }
                )
            }
        }
    }

    private fun loadEditForm(id: Int? = this.id) = viewModelScope.launch {
        val forces = getForcesUseCase().getOrElse { emptyList() }
        val levels = getLevelsUseCase().getOrElse { emptyList() }
        val mechanics = getMechanicUseCase().getOrElse { emptyList() }
        val equipments = getEquipmentsUseCase().getOrElse { emptyList() }
        val categories = getCategoriesUseCase().getOrElse { emptyList() }
        val muscles = getMusclesUseCase().getOrElse { emptyList() }

        if (id != null) {
            combine(
                getExerciseDescriptionUseCase(id),
                getExerciseMusclesUseCase(id)
            ) { exerciseData, musclesData ->
                ExerciseEditUiState.ExerciseForm(
                    exercise = ExerciseEditItem(
                        id = exerciseData.id,
                        name = exerciseData.name,
                        description = exerciseData.description,
                        rule = exerciseData.rule,
                        force = forces.fastFirstOrNull { it.name == exerciseData.force },
                        level = levels.fastFirstOrNull { it.name == exerciseData.level },
                        mechanic = mechanics.fastFirstOrNull { it.name == exerciseData.mechanic },
                        equipment = equipments.fastFirstOrNull { it.name == exerciseData.equipment },
                        category = categories.fastFirstOrNull { it.name == exerciseData.category }
                    ),
                    forces = listOf(null) + forces,
                    levels = listOf(null) + levels,
                    mechanics = listOf(null) + mechanics,
                    equipments = listOf(null) + equipments,
                    categories = listOf(null) + categories,
                    muscles =  if (musclesData.isNotEmpty()) {
                        muscles.map { muscle ->
                            val exMuscle = musclesData.find { it.muscleId == muscle.muscleId }

                            muscle.copy(
                                checked = exMuscle != null,
                                value = exMuscle?.value ?: muscle.value
                            )
                        }

                    } else {
                        muscles
                    }
                )
            }
                .catch { error ->
                    _state.value = ExerciseEditUiState.Error(error.message ?: "Unknown error")
                }
                .collect { state ->
                    _state.value = state
                }

        }
        else {
            _state.value = ExerciseEditUiState.ExerciseForm(
                exercise = ExerciseEditItem(
                    force = null,
                    level = null,
                    mechanic = null,
                    equipment = null,
                    category = null
                ),
                forces = listOf(null) + forces,
                levels = listOf(null) + levels,
                mechanics = listOf(null) + mechanics,
                equipments = listOf(null) + equipments,
                categories = listOf(null) + categories,
                muscles = muscles
            )
        }
    }

    private fun saveExercise(function: (Int) -> Unit) = viewModelScope.launch {
        val s = _state.value as? ExerciseEditUiState.ExerciseForm ?: return@launch

        if (s.exercise.name.isEmpty()) updateIfForm { it.copy(exercise = it.exercise.copy(isError = true)) }
        else if (checkNameExerciseExistsUseCase(
                s.exercise.name,
                s.exercise.id ?: 0
            ).getOrElse { true }
        ) {
            updateIfForm { it.copy(exercise = it.exercise.copy(isError = true)) }
        } else {
            upsertCustomExerciseUseCase(
                ex = Exercises(
                    id = s.exercise.id ?: 0,
                    icon = "",
                    isCustom = true,
                    forceId = s.exercise.force?.forceId,
                    levelId = s.exercise.level?.levelId,
                    equipmentId = s.exercise.equipment?.eqId,
                    mechanicId = s.exercise.mechanic?.mechanicId,
                    categoryId = s.exercise.category?.categoryId,
                ),
                exData = ExerciseTranslations(
                    exerciseId = s.exercise.id ?: 0,
                    language = Locale.current.language,
                    name = s.exercise.name,
                    description = s.exercise.description,
                    rule = s.exercise.rule
                ),
                exMuscle = s.muscles
                    .filter {
                        it.checked && it.value > 0
                    }
                    .map {
                        ExerciseMuscles(
                            muscleId = it.muscleId,
                            exerciseId = s.exercise.id ?: 0,
                            value = it.value
                        )
                    }
            ).fold(
                onSuccess = { id ->
                    updateIfForm { it.copy(exercise = it.exercise.copy(id = id)) }
                    function(id)
                },
                onFailure = { error -> _state.value = ExerciseEditUiState.Error(error.toString()) }
            )
        }
    }

    private fun openDialogGuide(title: String, description: String) {
        updateIfForm {
            it.copy(
                dialogContent = ExerciseDialogContent(
                    id = -1,
                    name = title,
                    description = description,
                )
            )
        }
    }

    private fun updateIfForm(transform: (ExerciseEditUiState.ExerciseForm) -> ExerciseEditUiState.ExerciseForm) {
        _state.update { currentState ->
            if (currentState is ExerciseEditUiState.ExerciseForm) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}