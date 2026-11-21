package com.example

import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.data.LevelCatalog
import com.example.engine.EulerSolver
import com.example.engine.FailureReason
import com.example.engine.PuzzleStatus
import com.example.ui.AppScreen
import com.example.ui.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readAppNameFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("One Line", appName)
    }

    @Test
    fun gameplayEngineSolvesLevel1AndDetectsInvalidMoves() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = GameViewModel(app)

        vm.finishSplash()
        vm.completeOnboarding()
        assertEquals(AppScreen.Home, vm.currentScreen.value)

        // Start Level 1 and verify lifting finger mid-draw triggers LIFTED_FINGER failure
        vm.startLevel(1)
        assertEquals(AppScreen.Gameplay, vm.currentScreen.value)
        val level1 = LevelCatalog.getLevel(1)
        val path = EulerSolver.findEulerPath(level1)
        assertTrue(path.size == 4)

        vm.beginStrokeAt(path[0])
        assertTrue(vm.tryMoveTo(path[1]))
        vm.onLiftFinger()
        assertEquals(PuzzleStatus.FAILED, vm.puzzleState.value.status)
        assertEquals(FailureReason.LIFTED_FINGER, vm.puzzleState.value.failureReason)

        // Retry and complete all edges in one continuous stroke
        vm.restartPuzzle()
        assertEquals(PuzzleStatus.READY, vm.puzzleState.value.status)
        vm.beginStrokeAt(path[0])
        for (i in 1 until path.size) {
            assertTrue("Step $i should succeed", vm.tryMoveTo(path[i]))
        }
        assertEquals(PuzzleStatus.COMPLETE, vm.puzzleState.value.status)

        // Pump MainLooper & IO DataStore coroutine
        repeat(20) {
            shadowOf(Looper.getMainLooper()).idle()
            if (vm.puzzleState.value.completionResult != null && (vm.progress.value.levelStars[1] ?: 0) > 0) {
                return@repeat
            }
            delay(50L)
        }
        shadowOf(Looper.getMainLooper()).idle()

        assertNotNull(vm.puzzleState.value.completionResult)
        assertTrue((vm.progress.value.levelStars[1] ?: 0) in 1..3)
        assertTrue(vm.progress.value.highestUnlockedLevel >= 2)
    }
}
