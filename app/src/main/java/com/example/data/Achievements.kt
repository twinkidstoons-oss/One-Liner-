package com.example.data

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val coinReward: Int,
    val progressSelector: (GameProgress) -> Int,
)

object Achievements {
    val all: List<AchievementDef> = listOf(
        AchievementDef(
            id = "first_stroke",
            title = "First Stroke",
            description = "Complete your first One-Line puzzle",
            target = 1,
            coinReward = 20,
            progressSelector = { it.completedLevelsCount },
        ),
        AchievementDef(
            id = "tier1_master",
            title = "Foundation Spark",
            description = "Complete 10 unique puzzle levels",
            target = 10,
            coinReward = 50,
            progressSelector = { it.completedLevelsCount },
        ),
        AchievementDef(
            id = "halfway_hero",
            title = "Halfway Architect",
            description = "Complete 50 puzzle levels",
            target = 50,
            coinReward = 150,
            progressSelector = { it.completedLevelsCount },
        ),
        AchievementDef(
            id = "grandmaster",
            title = "One-Line Grandmaster",
            description = "Complete all 100 puzzle levels",
            target = 100,
            coinReward = 350,
            progressSelector = { it.completedLevelsCount },
        ),
        AchievementDef(
            id = "perfectionist",
            title = "Perfectionist",
            description = "Earn 3 stars on 10 different levels",
            target = 10,
            coinReward = 75,
            progressSelector = { it.threeStarCount },
        ),
        AchievementDef(
            id = "star_collector",
            title = "Constellation",
            description = "Collect 150 total stars across levels",
            target = 150,
            coinReward = 150,
            progressSelector = { it.totalStars },
        ),
        AchievementDef(
            id = "daily_devotee",
            title = "Daily Challenger",
            description = "Complete 3 Daily Challenges",
            target = 3,
            coinReward = 80,
            progressSelector = { it.completedDailyDates.size },
        ),
        AchievementDef(
            id = "pure_genius",
            title = "Pure Intuition",
            description = "Solve 15 puzzles without using hints",
            target = 15,
            coinReward = 100,
            progressSelector = { it.zeroHintClears },
        ),
    )

    fun evaluateNewlyUnlocked(projectedProgress: GameProgress): List<AchievementDef> {
        return all.filter { def ->
            !projectedProgress.unlockedAchievements.contains(def.id) &&
                def.progressSelector(projectedProgress) >= def.target
        }
    }
}
