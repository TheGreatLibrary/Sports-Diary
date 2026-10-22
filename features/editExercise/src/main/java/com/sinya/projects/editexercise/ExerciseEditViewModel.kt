package com.sinya.projects.editexercise

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.util.fastFirstOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.error.EmptyTitleError
import com.sinya.projects.domain.error.SavedChanges
import com.sinya.projects.domain.error.SavingProcess
import com.sinya.projects.domain.error.TitleExistedError
import com.sinya.projects.domain.model.ExerciseDialogContent
import com.sinya.projects.domain.model.ExerciseEditItem
import com.sinya.projects.domain.model.ExerciseMusclesEntry
import com.sinya.projects.domain.model.ExerciseTranslationsEntry
import com.sinya.projects.domain.model.ExerciseWithFullData
import com.sinya.projects.domain.model.ExercisesEntry
import com.sinya.projects.domain.useCase.CheckNameExerciseExistsUseCase
import com.sinya.projects.domain.useCase.GetCategoriesUseCase
import com.sinya.projects.domain.useCase.GetEquipmentsUseCase
import com.sinya.projects.domain.useCase.GetExerciseMusclesUseCase
import com.sinya.projects.domain.useCase.GetForcesUseCase
import com.sinya.projects.domain.useCase.GetLevelsUseCase
import com.sinya.projects.domain.useCase.GetMechanicsUseCase
import com.sinya.projects.domain.useCase.GetMusclesUseCase
import com.sinya.projects.domain.useCase.ObserveExerciseDescriptionUseCase
import com.sinya.projects.domain.useCase.UpsertCustomExerciseUseCase
import com.sinya.projects.domain.utils.UndoManager
import com.sinya.projects.domain.utils.getErrorMessage
import com.sinya.projects.editexercise.subScreen.saveImageToInternalStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel(assistedFactory = ExerciseEditViewModel.Factory::class)
internal class ExerciseEditViewModel @AssistedInject constructor(
    @Assisted("id") val id: Int?,

    private val getExerciseDescriptionUseCase: ObserveExerciseDescriptionUseCase,
    private val getExerciseMusclesUseCase: GetExerciseMusclesUseCase,
    private val getForcesUseCase: GetForcesUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getMechanicUseCase: GetMechanicsUseCase,
    private val getEquipmentsUseCase: GetEquipmentsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMusclesUseCase: GetMusclesUseCase,

    private val upsertCustomExerciseUseCase: UpsertCustomExerciseUseCase,
    private val checkNameExerciseExistsUseCase: CheckNameExerciseExistsUseCase
) : ViewModel() {

    private val undoManager = UndoManager<ExerciseEditUiState.ExerciseForm>()
    private var undoJob: Job? = null

    private val _state = MutableStateFlow<ExerciseEditUiState>(ExerciseEditUiState.Loading)
    val state: StateFlow<ExerciseEditUiState> = _state.asStateFlow()

    private val exerciseId = MutableStateFlow(id)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") id: Int?
        ): ExerciseEditViewModel
    }

    init {
        observeExerciseEntity()
        observeSavingChanges()
    }

    private fun observeExerciseEntity() = viewModelScope.launch {
        val forces = getForcesUseCase().getOrElse { emptyList() }
        val levels = getLevelsUseCase().getOrElse { emptyList() }
        val mechanics = getMechanicUseCase().getOrElse { emptyList() }
        val equipments = getEquipmentsUseCase().getOrElse { emptyList() }
        val categories = getCategoriesUseCase().getOrElse { emptyList() }
        val muscles = getMusclesUseCase().getOrElse { emptyList() }

        exerciseId
            .flatMapLatest { id ->
                when (id) {
                    null -> flowOf(
                        ExerciseWithFullData(
                            id = 0,
                            name = "",
                            description = "",
                            isCustom = true,
                            rule = "",
                            force = null,
                            level = null,
                            mechanic = null,
                            equipment = null,
                            category = null,
                            icon = ""
                        ) to emptyList()
                    )

                    else -> combine(
                        getExerciseDescriptionUseCase(id),
                        getExerciseMusclesUseCase(id)
                    ) { exerciseData, musclesData ->
                        exerciseData to musclesData
                    }

                }
            }
            .onEach { (exerciseData, musclesData) ->
                val newState = ExerciseEditUiState.ExerciseForm(
                    exercise = ExerciseEditItem(
                        id = exerciseData.id,
                        name = exerciseData.name,
                        description = exerciseData.description,
                        rule = exerciseData.rule,
                        icon = exerciseData.icon,
                        force = forces.fastFirstOrNull { it.name == exerciseData.force },
                        level = levels.fastFirstOrNull { it.name == exerciseData.level },
                        mechanic = mechanics.fastFirstOrNull { it.name == exerciseData.mechanic },
                        equipment = equipments.fastFirstOrNull { it.name == exerciseData.equipment },
                        category = categories.fastFirstOrNull { it.name == exerciseData.category },
                        isCustom = exerciseData.isCustom,
                    ),
                    forces = listOf(null) + forces,
                    levels = listOf(null) + levels,
                    mechanics = listOf(null) + mechanics,
                    equipments = listOf(null) + equipments,
                    categories = listOf(null) + categories,
                    muscles = if (musclesData.isNotEmpty()) {
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

                _state.update { currentState ->
                    when (currentState) {
                        is ExerciseEditUiState.Error -> newState
                        ExerciseEditUiState.Loading -> newState
                        is ExerciseEditUiState.ExerciseForm -> currentState.copy(
                            exercise = ExerciseEditItem(
                                id = exerciseData.id,
                                name = exerciseData.name,
                                description = exerciseData.description,
                                rule = exerciseData.rule,
                                icon = exerciseData.icon,
                                force = forces.fastFirstOrNull { it.name == exerciseData.force },
                                level = levels.fastFirstOrNull { it.name == exerciseData.level },
                                mechanic = mechanics.fastFirstOrNull { it.name == exerciseData.mechanic },
                                equipment = equipments.fastFirstOrNull { it.name == exerciseData.equipment },
                                isCustom = exerciseData.isCustom,
                                category = categories.fastFirstOrNull { it.name == exerciseData.category }
                            ),
                            muscles = if (musclesData.isNotEmpty()) {
                                muscles.map { muscle ->
                                    val exMuscle =
                                        musclesData.find { it.muscleId == muscle.muscleId }

                                    muscle.copy(
                                        checked = exMuscle != null,
                                        value = exMuscle?.value ?: muscle.value
                                    )
                                }
                            } else {
                                muscles
                            }
                        )

                        else -> currentState
                    }
                }
            }
            .catch { error ->
                _state.update { ExerciseEditUiState.Error(error.message ?: "Unknown error") }
            }
            .collect()
    }

    private fun observeSavingChanges() = viewModelScope.launch {
        _state
            .filterIsInstance<ExerciseEditUiState.ExerciseForm>()
            .filter { it.hasUnsavedChanges && !it.isSaving }
            .map { it.exercise to it.muscles }
            .distinctUntilChanged()
            .filter { it.first.name.isNotBlank() }
            .debounce(2000)
            .onEach { showMessage(null, SavingProcess()) }
            .collectLatest {
                updateIfForm { it.copy(isSaving = true) }
                saveExercise {
                    updateIfForm {
                        it.copy(
                            hasUnsavedChanges = false,
                            isSaving = false
                        )
                    }
                }
            }
    }

    fun onEvent(event: ExerciseEditEvent) {
        when (event) {
            ExerciseEditEvent.Save -> saveExercise { id ->
                _state.update { ExerciseEditUiState.Success(id) }
            }

            is ExerciseEditEvent.OpenDialogGuide -> openDialogGuide(event.title, event.descr)

            ExerciseEditEvent.DialogShown -> updateIfForm { it.copy(dialogContent = null) }

            is ExerciseEditEvent.ShowError -> showMessage(null, event.message)

            is ExerciseEditEvent.Undoable -> handleUndoable(event.action)

            is ExerciseEditEvent.Undo -> undo()

            is ExerciseEditEvent.OnImagePicked -> {
                val state = _state.value as? ExerciseEditUiState.ExerciseForm ?: return
                viewModelScope.launch(Dispatchers.IO) {
                    val localPathString = saveImageToInternalStorage(event.context, event.uri)

                    if (localPathString != null) {
                        handleUndoable(ExerciseUndoableAction.OnIconChange(
                            state.exercise.icon,
                            localPathString.path
                        ))
                    }
                }
            }

            is ExerciseEditEvent.Redo -> redo()
        }
    }

    private fun handleUndoable(action: ExerciseUndoableAction) {
        undoManager.push(action)
        updateIfForm {
            action.apply(it).copy(
                canUndo = undoManager.canUndo,
                canRedo = undoManager.canRedo
            )
        }
    }

    fun undo() {
        if (undoJob?.isActive == true) return
        undoJob = viewModelScope.launch {
            updateIfForm { it.copy(isUndoing = true) }
            delay(200)
            updateIfForm { form ->
                (undoManager.undo(form) ?: form).copy(
                    canUndo = undoManager.canUndo,
                    canRedo = undoManager.canRedo
                )
            }
            updateIfForm { it.copy(isUndoing = false) }
        }
    }

    fun redo() {
        if (undoJob?.isActive == true) return
        undoJob = viewModelScope.launch {
            updateIfForm { it.copy(isUndoing = true) }
            delay(200)
            updateIfForm { form ->
                (undoManager.redo(form) ?: form).copy(
                    canUndo = undoManager.canUndo,
                    canRedo = undoManager.canRedo
                )
            }
            updateIfForm { it.copy(isUndoing = false) }
        }
    }

    private fun checkTitleCategory(function: () -> Unit) {
        val s = _state.value as? ExerciseEditUiState.ExerciseForm ?: return

        viewModelScope.launch {
            when {
                s.exercise.name.isEmpty() -> showMessage(true, EmptyTitleError())

                checkNameExerciseExistsUseCase(
                    s.exercise.name,
                    s.exercise.id ?: 0
                ).getOrElse { true } -> showMessage(true, TitleExistedError())

                else -> function()
            }
        }
    }

    private fun saveExercise(function: (Int) -> Unit) {
        val s = _state.value as? ExerciseEditUiState.ExerciseForm ?: return

        checkTitleCategory {
            viewModelScope.launch {
                upsertCustomExerciseUseCase(
                    ex = ExercisesEntry(
                        id = s.exercise.id ?: 0,
                        icon = s.exercise.icon ?: "",
                        isCustom = s.exercise.isCustom,
                        forceId = s.exercise.force?.id,
                        levelId = s.exercise.level?.id,
                        equipmentId = s.exercise.equipment?.id,
                        mechanicId = s.exercise.mechanic?.id,
                        categoryId = s.exercise.category?.id,
                    ),
                    exData = ExerciseTranslationsEntry(
                        id = s.exercise.id ?: 0,
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
                            ExerciseMusclesEntry(
                                muscleId = it.muscleId,
                                exerciseId = s.exercise.id ?: 0,
                                value = it.value
                            )
                        }
                ).fold(
                    onSuccess = { id ->
                        updateIfForm {
                            it.copy(
                                exercise = it.exercise.copy(id = id),
                                errorMessage = SavedChanges().getErrorMessage()
                            )
                        }
                        exerciseId.update { id }
                        function(id)
                    },
                    onFailure = { error -> showMessage(null, error) }
                )
            }
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

    private fun showMessage(isError: Boolean?, message: Throwable?) = updateIfForm {
        it.copy(
            exercise = it.exercise.copy(isError = isError ?: it.exercise.isError),
            errorMessage = message?.getErrorMessage()
        )
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