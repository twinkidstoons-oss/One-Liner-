package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(name = "one_line_progress")

data class GameSettings(
    val soundEnabled: Boolean = true,
    val sfxVolume: Float = 0.85f,
    val musicEnabled: Boolean = true,
    val musicVolume: Float = 0.45f,
    val vibrationEnabled: Boolean = true,
    val languageCode: String = "en",
)

data class GameProgress(
    val onboardingCompleted: Boolean = false,
    val coins: Int = 50,
    val highestUnlockedLevel: Int = 1,
    val levelStars: Map<Int, Int> = emptyMap(),
    val levelBestScores: Map<Int, Int> = emptyMap(),
    val completedDailyDates: Set<String> = emptySet(),
    val dailyStreak: Int = 0,
    val lastDailyDate: String = "",
    val totalAttempts: Int = 0,
    val totalClears: Int = 0,
    val zeroHintClears: Int = 0,
    val unlockedAchievements: Set<String> = emptySet(),
    val settings: GameSettings = GameSettings(),
) {
    val completedLevelsCount: Int get() = levelStars.size
    val totalStars: Int get() = levelStars.values.sum()
    val threeStarCount: Int get() = levelStars.values.count { it >= 3 }
}

class GameProgressRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val COINS = intPreferencesKey("coins")
        val HIGHEST_UNLOCKED = intPreferencesKey("highest_unlocked_level")
        val LEVEL_STARS_JSON = stringPreferencesKey("level_stars_json")
        val LEVEL_SCORES_JSON = stringPreferencesKey("level_scores_json")
        val DAILY_DATES_JSON = stringPreferencesKey("daily_dates_json")
        val DAILY_STREAK = intPreferencesKey("daily_streak")
        val LAST_DAILY_DATE = stringPreferencesKey("last_daily_date")
        val TOTAL_ATTEMPTS = intPreferencesKey("total_attempts")
        val TOTAL_CLEARS = intPreferencesKey("total_clears")
        val ZERO_HINT_CLEARS = intPreferencesKey("zero_hint_clears")
        val ACHIEVEMENTS_JSON = stringPreferencesKey("achievements_json")

        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SFX_VOLUME = floatPreferencesKey("sfx_volume")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    val progressFlow: Flow<GameProgress> = context.gameDataStore.data.map { prefs ->
        GameProgress(
            onboardingCompleted = prefs[Keys.ONBOARDING_DONE] ?: false,
            coins = prefs[Keys.COINS] ?: 50,
            highestUnlockedLevel = (prefs[Keys.HIGHEST_UNLOCKED] ?: 1).coerceIn(1, 50),
            levelStars = parseIntMap(prefs[Keys.LEVEL_STARS_JSON]),
            levelBestScores = parseIntMap(prefs[Keys.LEVEL_SCORES_JSON]),
            completedDailyDates = parseStringSet(prefs[Keys.DAILY_DATES_JSON]),
            dailyStreak = prefs[Keys.DAILY_STREAK] ?: 0,
            lastDailyDate = prefs[Keys.LAST_DAILY_DATE] ?: "",
            totalAttempts = prefs[Keys.TOTAL_ATTEMPTS] ?: 0,
            totalClears = prefs[Keys.TOTAL_CLEARS] ?: 0,
            zeroHintClears = prefs[Keys.ZERO_HINT_CLEARS] ?: 0,
            unlockedAchievements = parseStringSet(prefs[Keys.ACHIEVEMENTS_JSON]),
            settings = GameSettings(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                sfxVolume = prefs[Keys.SFX_VOLUME] ?: 0.85f,
                musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
                musicVolume = prefs[Keys.MUSIC_VOLUME] ?: 0.45f,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
                languageCode = prefs[Keys.LANGUAGE_CODE] ?: "en",
            ),
        )
    }

    suspend fun setOnboardingCompleted() {
        context.gameDataStore.edit { it[Keys.ONBOARDING_DONE] = true }
    }

    suspend fun incrementAttempt() {
        context.gameDataStore.edit { prefs ->
            prefs[Keys.TOTAL_ATTEMPTS] = (prefs[Keys.TOTAL_ATTEMPTS] ?: 0) + 1
        }
    }

    suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.gameDataStore.edit { prefs ->
            val current = prefs[Keys.COINS] ?: 50
            if (current >= amount) {
                prefs[Keys.COINS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun recordLevelComplete(
        levelId: Int,
        starsEarned: Int,
        scoreEarned: Int,
        coinsEarned: Int,
        usedHint: Boolean,
        newlyUnlockedAchievementIds: List<String>,
        achievementBonusCoins: Int,
    ) {
        context.gameDataStore.edit { prefs ->
            val starsMap = parseIntMap(prefs[Keys.LEVEL_STARS_JSON]).toMutableMap()
            val prevStars = starsMap[levelId] ?: 0
            starsMap[levelId] = maxOf(prevStars, starsEarned)
            prefs[Keys.LEVEL_STARS_JSON] = encodeIntMap(starsMap)

            val scoresMap = parseIntMap(prefs[Keys.LEVEL_SCORES_JSON]).toMutableMap()
            val prevScore = scoresMap[levelId] ?: 0
            scoresMap[levelId] = maxOf(prevScore, scoreEarned)
            prefs[Keys.LEVEL_SCORES_JSON] = encodeIntMap(scoresMap)

            val prevUnlocked = prefs[Keys.HIGHEST_UNLOCKED] ?: 1
            if (levelId >= prevUnlocked && levelId < 50) {
                prefs[Keys.HIGHEST_UNLOCKED] = levelId + 1
            }

            prefs[Keys.COINS] = (prefs[Keys.COINS] ?: 50) + coinsEarned + achievementBonusCoins
            prefs[Keys.TOTAL_CLEARS] = (prefs[Keys.TOTAL_CLEARS] ?: 0) + 1
            if (!usedHint) {
                prefs[Keys.ZERO_HINT_CLEARS] = (prefs[Keys.ZERO_HINT_CLEARS] ?: 0) + 1
            }

            if (newlyUnlockedAchievementIds.isNotEmpty()) {
                val achievements = parseStringSet(prefs[Keys.ACHIEVEMENTS_JSON]).toMutableSet()
                achievements.addAll(newlyUnlockedAchievementIds)
                prefs[Keys.ACHIEVEMENTS_JSON] = encodeStringSet(achievements)
            }
        }
    }

    suspend fun recordDailyComplete(
        dateKey: String,
        yesterdayKey: String,
        coinsEarned: Int,
        usedHint: Boolean,
        newlyUnlockedAchievementIds: List<String>,
        achievementBonusCoins: Int,
    ) {
        context.gameDataStore.edit { prefs ->
            val dates = parseStringSet(prefs[Keys.DAILY_DATES_JSON]).toMutableSet()
            val isFirstTimeToday = dates.add(dateKey)
            prefs[Keys.DAILY_DATES_JSON] = encodeStringSet(dates)

            if (isFirstTimeToday) {
                val lastDate = prefs[Keys.LAST_DAILY_DATE] ?: ""
                val prevStreak = prefs[Keys.DAILY_STREAK] ?: 0
                prefs[Keys.DAILY_STREAK] = if (lastDate == yesterdayKey) prevStreak + 1 else 1
                prefs[Keys.LAST_DAILY_DATE] = dateKey
            }

            prefs[Keys.COINS] = (prefs[Keys.COINS] ?: 50) + coinsEarned + achievementBonusCoins
            prefs[Keys.TOTAL_CLEARS] = (prefs[Keys.TOTAL_CLEARS] ?: 0) + 1
            if (!usedHint) {
                prefs[Keys.ZERO_HINT_CLEARS] = (prefs[Keys.ZERO_HINT_CLEARS] ?: 0) + 1
            }
            if (newlyUnlockedAchievementIds.isNotEmpty()) {
                val achievements = parseStringSet(prefs[Keys.ACHIEVEMENTS_JSON]).toMutableSet()
                achievements.addAll(newlyUnlockedAchievementIds)
                prefs[Keys.ACHIEVEMENTS_JSON] = encodeStringSet(achievements)
            }
        }
    }

    suspend fun updateSettings(transform: (GameSettings) -> GameSettings) {
        context.gameDataStore.edit { prefs ->
            val current = GameSettings(
                soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
                sfxVolume = prefs[Keys.SFX_VOLUME] ?: 0.85f,
                musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
                musicVolume = prefs[Keys.MUSIC_VOLUME] ?: 0.45f,
                vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
                languageCode = prefs[Keys.LANGUAGE_CODE] ?: "en",
            )
            val updated = transform(current)
            prefs[Keys.SOUND_ENABLED] = updated.soundEnabled
            prefs[Keys.SFX_VOLUME] = updated.sfxVolume
            prefs[Keys.MUSIC_ENABLED] = updated.musicEnabled
            prefs[Keys.MUSIC_VOLUME] = updated.musicVolume
            prefs[Keys.VIBRATION_ENABLED] = updated.vibrationEnabled
            prefs[Keys.LANGUAGE_CODE] = updated.languageCode
        }
    }

    suspend fun resetProgress() {
        context.gameDataStore.edit { prefs ->
            prefs[Keys.COINS] = 50
            prefs[Keys.HIGHEST_UNLOCKED] = 1
            prefs[Keys.LEVEL_STARS_JSON] = "{}"
            prefs[Keys.LEVEL_SCORES_JSON] = "{}"
            prefs[Keys.DAILY_DATES_JSON] = "[]"
            prefs[Keys.DAILY_STREAK] = 0
            prefs[Keys.LAST_DAILY_DATE] = ""
            prefs[Keys.TOTAL_ATTEMPTS] = 0
            prefs[Keys.TOTAL_CLEARS] = 0
            prefs[Keys.ZERO_HINT_CLEARS] = 0
            prefs[Keys.ACHIEVEMENTS_JSON] = "[]"
        }
    }

    private fun parseIntMap(json: String?): Map<Int, Int> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            val obj = JSONObject(json)
            val result = HashMap<Int, Int>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                k.toIntOrNull()?.let { id -> result[id] = obj.optInt(k, 0) }
            }
            result
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun encodeIntMap(map: Map<Int, Int>): String {
        val obj = JSONObject()
        for ((k, v) in map) obj.put(k.toString(), v)
        return obj.toString()
    }

    private fun parseStringSet(json: String?): Set<String> {
        if (json.isNullOrBlank()) return emptySet()
        return try {
            val arr = JSONArray(json)
            val set = LinkedHashSet<String>()
            for (i in 0 until arr.length()) {
                set.add(arr.getString(i))
            }
            set
        } catch (_: Exception) {
            emptySet()
        }
    }

    private fun encodeStringSet(set: Set<String>): String {
        val arr = JSONArray()
        for (item in set) arr.put(item)
        return arr.toString()
    }
}
