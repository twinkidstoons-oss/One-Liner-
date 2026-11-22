package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEngine
import com.example.data.AchievementDef
import com.example.data.Achievements
import com.example.data.GameProgress
import com.example.data.GameProgressRepository
import com.example.data.GameSettings
import com.example.data.LevelCatalog
import com.example.engine.EulerSolver
import com.example.engine.FailureReason
import com.example.engine.PuzzleLevel
import com.example.engine.PuzzleStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed interface AppScreen {
    data object Splash : AppScreen
    data object Onboarding : AppScreen
    data object Home : AppScreen
    data object LevelSelect : AppScreen
    data object Gameplay : AppScreen
    data object DailyChallenge : AppScreen
    data object Statistics : AppScreen
    data object Settings : AppScreen
    data object HowToPlay : AppScreen
}

data class CompletionResult(
    val stars: Int,
    val score: Int,
    val coinsEarned: Int,
    val isFirstClear: Boolean,
    val newlyUnlockedAchievements: List<AchievementDef>,
)

data class ActivePuzzleState(
    val level: PuzzleLevel = LevelCatalog.getLevel(1),
    val status: PuzzleStatus = PuzzleStatus.READY,
    val currentVertexId: Int? = null,
    val visitedPath: List<Int> = emptyList(),
    val drawnEdgeIds: Set<Int> = emptySet(),
    val hintPath: List<Int> = emptyList(),
    val revealedHintSteps: Int = 0,
    val hintsUsedThisLevel: Int = 0,
    val attemptsThisLevel: Int = 1,
    val elapsedSeconds: Int = 0,
    val failureReason: FailureReason? = null,
    val invalidEdgeFlashId: Int? = null,
    val invalidFlashCounter: Int = 0,
    val completionResult: CompletionResult? = null,
    val attemptNonce: Int = 0,
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameProgressRepository(application)
    val soundEngine = SoundEngine(application)

    val progress: StateFlow<GameProgress> = repository.progressFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameProgress(),
    )

    private var screenStack: List<AppScreen> = listOf(AppScreen.Splash)
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private fun updateScreenStack(transform: (List<AppScreen>) -> List<AppScreen>) {
        screenStack = transform(screenStack)
        _currentScreen.value = screenStack.lastOrNull() ?: AppScreen.Home
    }

    private val _puzzleState = MutableStateFlow(ActivePuzzleState())
    val puzzleState: StateFlow<ActivePuzzleState> = _puzzleState.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            progress.collect { p ->
                soundEngine.updateSettings(
                    soundEnabled = p.settings.soundEnabled,
                    sfxVol = p.settings.sfxVolume,
                    musicOn = p.settings.musicEnabled,
                    musicVol = p.settings.musicVolume,
                    vibrationOn = p.settings.vibrationEnabled,
                )
            }
        }
    }

    fun onAppForegroundChanged(isForeground: Boolean) {
        soundEngine.onForegroundChanged(isForeground)
    }

    fun finishSplash() {
        val dest = if (progress.value.onboardingCompleted) AppScreen.Home else AppScreen.Onboarding
        updateScreenStack { listOf(dest) }
    }

    fun completeOnboarding() {
        soundEngine.playClick()
        viewModelScope.launch {
            repository.setOnboardingCompleted()
        }
        updateScreenStack { listOf(AppScreen.Home) }
    }

    fun navigateTo(screen: AppScreen) {
        soundEngine.playClick()
        updateScreenStack { stack ->
            if (stack.lastOrNull() == screen) stack else stack + screen
        }
    }

    fun navigateBack() {
        soundEngine.playClick()
        val current = screenStack.lastOrNull()
        if (current == AppScreen.Gameplay) {
            stopTimer()
        }
        updateScreenStack { stack ->
            if (stack.size > 1) stack.dropLast(1) else listOf(AppScreen.Home)
        }
    }

    fun navigateHome() {
        soundEngine.playClick()
        stopTimer()
        updateScreenStack { listOf(AppScreen.Home) }
    }

    fun startLevel(levelId: Int) {
        val level = LevelCatalog.getLevel(levelId)
        startPuzzle(level)
    }

    fun startDailyChallenge(dateKey: String = todayDateKey()) {
        val level = LevelCatalog.generateDailyPuzzle(dateKey)
        startPuzzle(level)
    }

    private fun startPuzzle(level: PuzzleLevel) {
        soundEngine.playClick()
        val solution = EulerSolver.findEulerPath(level)
        _puzzleState.value = ActivePuzzleState(
            level = level,
            status = PuzzleStatus.READY,
            hintPath = solution,
            revealedHintSteps = 0,
            hintsUsedThisLevel = 0,
            attemptsThisLevel = 1,
            elapsedSeconds = 0,
            attemptNonce = _puzzleState.value.attemptNonce + 1,
        )
        startTimer()
        updateScreenStack { stack ->
            if (stack.lastOrNull() == AppScreen.Gameplay) stack else stack + AppScreen.Gameplay
        }
    }

    fun restartPuzzle(countAsNewAttempt: Boolean = true) {
        soundEngine.playClick()
        val current = _puzzleState.value
        _puzzleState.value = current.copy(
            status = PuzzleStatus.READY,
            currentVertexId = null,
            visitedPath = emptyList(),
            drawnEdgeIds = emptySet(),
            attemptsThisLevel = if (countAsNewAttempt) current.attemptsThisLevel + 1 else current.attemptsThisLevel,
            failureReason = null,
            invalidEdgeFlashId = null,
            completionResult = null,
            attemptNonce = current.attemptNonce + 1,
        )
        startTimer()
    }

    fun pausePuzzle() {
        val state = _puzzleState.value
        if (state.status == PuzzleStatus.READY || state.status == PuzzleStatus.DRAWING) {
            soundEngine.playClick()
            stopTimer()
            _puzzleState.update { it.copy(status = PuzzleStatus.PAUSED) }
        }
    }

    fun resumePuzzle() {
        val state = _puzzleState.value
        if (state.status == PuzzleStatus.PAUSED) {
            soundEngine.playClick()
            _puzzleState.update {
                it.copy(
                    status = PuzzleStatus.READY,
                    currentVertexId = null,
                    visitedPath = emptyList(),
                    drawnEdgeIds = emptySet(),
                    attemptNonce = it.attemptNonce + 1,
                )
            }
            startTimer()
        }
    }

    fun beginStrokeAt(vertexId: Int) {
        val state = _puzzleState.value
        if (state.status != PuzzleStatus.READY) return
        soundEngine.playDrawStart()
        viewModelScope.launch { repository.incrementAttempt() }
        _puzzleState.update {
            it.copy(
                status = PuzzleStatus.DRAWING,
                currentVertexId = vertexId,
                visitedPath = listOf(vertexId),
                drawnEdgeIds = emptySet(),
                failureReason = null,
            )
        }
    }

    /**
     * Attempts to extend the continuous stroke from [currentVertexId] to [targetVertexId].
     * Returns true if a valid edge was completed.
     */
    fun tryMoveTo(targetVertexId: Int): Boolean {
        val state = _puzzleState.value
        if (state.status != PuzzleStatus.DRAWING) return false
        val from = state.currentVertexId ?: return false
        if (from == targetVertexId) return false

        val undrawn = EulerSolver.findUndrawnEdge(state.level, from, targetVertexId, state.drawnEdgeIds)
        if (undrawn != null) {
            val newDrawn = state.drawnEdgeIds + undrawn.id
            val newPath = state.visitedPath + targetVertexId

            if (newDrawn.size == state.level.edges.size) {
                // Level solved!
                _puzzleState.update {
                    it.copy(
                        currentVertexId = targetVertexId,
                        visitedPath = newPath,
                        drawnEdgeIds = newDrawn,
                        status = PuzzleStatus.COMPLETE,
                    )
                }
                handleLevelComplete()
                return true
            }

            soundEngine.playEdgeConnect(newDrawn.size - 1)

            // Check if player walked into a dead end with remaining undrawn edges
            val hasNext = EulerSolver.hasAnyValidMoveFrom(state.level, targetVertexId, newDrawn)
            if (!hasNext) {
                _puzzleState.update {
                    it.copy(
                        currentVertexId = targetVertexId,
                        visitedPath = newPath,
                        drawnEdgeIds = newDrawn,
                    )
                }
                failAttempt(FailureReason.DEAD_END)
                return true
            }

            _puzzleState.update {
                it.copy(
                    currentVertexId = targetVertexId,
                    visitedPath = newPath,
                    drawnEdgeIds = newDrawn,
                )
            }
            return true
        }

        // Check if player tried to retrace an already drawn edge
        val drawnBetween = EulerSolver.findDrawnEdgeBetween(state.level, from, targetVertexId, state.drawnEdgeIds)
        if (drawnBetween != null) {
            // Prevent retracing: flash red & fail attempt if they dragged across an already drawn edge
            soundEngine.playInvalidMove()
            _puzzleState.update {
                it.copy(
                    invalidEdgeFlashId = drawnBetween.id,
                    invalidFlashCounter = it.invalidFlashCounter + 1,
                )
            }
            failAttempt(FailureReason.RETRACED_EDGE)
        } else {
            // No edge exists between from & targetVertexId -> prevent accidental outside path drawing
            soundEngine.playInvalidMove()
            _puzzleState.update {
                it.copy(invalidFlashCounter = it.invalidFlashCounter + 1)
            }
        }
        return false
    }

    fun onLiftFinger() {
        val state = _puzzleState.value
        if (state.status != PuzzleStatus.DRAWING) return
        if (state.drawnEdgeIds.isEmpty()) {
            // Accidental tap on a single node without drawing any edge: cleanly reset to READY
            _puzzleState.update {
                it.copy(
                    status = PuzzleStatus.READY,
                    currentVertexId = null,
                    visitedPath = emptyList(),
                    drawnEdgeIds = emptySet(),
                )
            }
            return
        }
        if (state.drawnEdgeIds.size < state.level.edges.size) {
            failAttempt(FailureReason.LIFTED_FINGER)
        }
    }

    private fun failAttempt(reason: FailureReason) {
        stopTimer()
        soundEngine.playLevelFail()
        _puzzleState.update {
            it.copy(
                status = PuzzleStatus.FAILED,
                failureReason = reason,
            )
        }
    }

    private fun handleLevelComplete() {
        stopTimer()
        soundEngine.playLevelComplete()
        val state = _puzzleState.value
        val level = state.level
        val currentProgress = progress.value

        val stars = when {
            state.hintsUsedThisLevel == 0 && state.attemptsThisLevel <= 2 -> 3
            state.hintsUsedThisLevel <= 1 && state.attemptsThisLevel <= 4 -> 2
            else -> 1
        }
        val timeBonus = maxOf(0, (level.parTimeSeconds - state.elapsedSeconds) * 12)
        val score = (level.edges.size * 100) + (stars * 150) + timeBonus - (state.hintsUsedThisLevel * 50)

        viewModelScope.launch {
            if (level.isDailyChallenge && level.dailyDateKey != null) {
                val dateKey = level.dailyDateKey
                val isFirstTime = !currentProgress.completedDailyDates.contains(dateKey)
                val coinsEarned = if (isFirstTime) 75 else 15

                val projected = currentProgress.copy(
                    completedDailyDates = currentProgress.completedDailyDates + dateKey,
                    totalClears = currentProgress.totalClears + 1,
                    zeroHintClears = currentProgress.zeroHintClears + if (state.hintsUsedThisLevel == 0) 1 else 0,
                )
                val newAch = Achievements.evaluateNewlyUnlocked(projected)
                val bonusCoins = newAch.sumOf { it.coinReward }

                repository.recordDailyComplete(
                    dateKey = dateKey,
                    yesterdayKey = yesterdayDateKey(),
                    coinsEarned = coinsEarned,
                    usedHint = state.hintsUsedThisLevel > 0,
                    newlyUnlockedAchievementIds = newAch.map { it.id },
                    achievementBonusCoins = bonusCoins,
                )
                soundEngine.playCoinReward()
                _puzzleState.update {
                    it.copy(
                        completionResult = CompletionResult(
                            stars = stars,
                            score = score.coerceAtLeast(100),
                            coinsEarned = coinsEarned + bonusCoins,
                            isFirstClear = isFirstTime,
                            newlyUnlockedAchievements = newAch,
                        ),
                    )
                }
            } else {
                val isFirstClear = !currentProgress.levelStars.containsKey(level.id)
                val baseCoins = 12 + (level.id / 5) * 3 + stars * 5
                val coinsEarned = baseCoins + if (isFirstClear) 15 else 0

                val updatedStars = currentProgress.levelStars.toMutableMap().apply {
                    put(level.id, maxOf(this[level.id] ?: 0, stars))
                }
                val projected = currentProgress.copy(
                    levelStars = updatedStars,
                    totalClears = currentProgress.totalClears + 1,
                    zeroHintClears = currentProgress.zeroHintClears + if (state.hintsUsedThisLevel == 0) 1 else 0,
                )
                val newAch = Achievements.evaluateNewlyUnlocked(projected)
                val bonusCoins = newAch.sumOf { it.coinReward }

                repository.recordLevelComplete(
                    levelId = level.id,
                    starsEarned = stars,
                    scoreEarned = score.coerceAtLeast(100),
                    coinsEarned = coinsEarned,
                    usedHint = state.hintsUsedThisLevel > 0,
                    newlyUnlockedAchievementIds = newAch.map { it.id },
                    achievementBonusCoins = bonusCoins,
                )
                soundEngine.playCoinReward()
                _puzzleState.update {
                    it.copy(
                        completionResult = CompletionResult(
                            stars = stars,
                            score = score.coerceAtLeast(100),
                            coinsEarned = coinsEarned + bonusCoins,
                            isFirstClear = isFirstClear,
                            newlyUnlockedAchievements = newAch,
                        ),
                    )
                }
            }
        }
    }

    fun useHint(notEnoughCoinsMessage: String) {
        val state = _puzzleState.value
        val maxSteps = (state.hintPath.size - 1).coerceAtLeast(0)
        if (state.revealedHintSteps >= maxSteps && state.status != PuzzleStatus.FAILED) {
            soundEngine.playClick()
            return
        }
        viewModelScope.launch {
            val spent = repository.spendCoins(HINT_COST)
            if (!spent) {
                soundEngine.playInvalidMove()
                _toastEvents.tryEmit(notEnoughCoinsMessage)
                return@launch
            }
            soundEngine.playCoinReward()
            val increment = 4
            val nextSteps = (state.revealedHintSteps + increment).coerceAtMost(maxSteps)
            _puzzleState.update {
                it.copy(
                    status = PuzzleStatus.READY,
                    currentVertexId = null,
                    visitedPath = emptyList(),
                    drawnEdgeIds = emptySet(),
                    failureReason = null,
                    revealedHintSteps = nextSteps,
                    hintsUsedThisLevel = it.hintsUsedThisLevel + 1,
                    attemptNonce = it.attemptNonce + 1,
                )
            }
            startTimer()
        }
    }

    fun updateSettings(transform: (GameSettings) -> GameSettings) {
        viewModelScope.launch {
            repository.updateSettings(transform)
        }
    }

    fun resetAllProgress() {
        soundEngine.playClick()
        viewModelScope.launch {
            repository.resetProgress()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val st = _puzzleState.value.status
                if (st == PuzzleStatus.READY || st == PuzzleStatus.DRAWING) {
                    _puzzleState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        soundEngine.release()
    }

    companion object {
        const val HINT_COST = 25

        fun todayDateKey(): String {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return fmt.format(Calendar.getInstance().time)
        }

        fun yesterdayDateKey(): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return fmt.format(cal.time)
        }

        fun recent7DateKeys(): List<String> {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return (6 downTo 0).map { offset ->
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -offset)
                fmt.format(cal.time)
            }
        }
    }
}
