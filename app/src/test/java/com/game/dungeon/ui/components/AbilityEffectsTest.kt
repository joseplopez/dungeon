package com.game.dungeon.ui.components

import com.game.dungeon.R
import com.game.dungeon.data.models.AIPriority
import com.game.dungeon.data.models.Hero
import com.game.dungeon.data.models.HeroClass
import com.game.dungeon.data.models.JobAbilityData
import com.game.dungeon.ui.viewmodels.DungeonViewModel.AbilityAnimationInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class AbilityEffectsTest {

    @Test
    fun testAbilityAnimationInfoDefaultLevel() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.BLACK_MAGE
        )
        assertEquals(1, animInfo.abilityLevel)
    }

    @Test
    fun testAbilityAnimationInfoCustomLevel() {
        val animInfo = AbilityAnimationInfo(
            heroClass = HeroClass.BLACK_MAGE,
            abilityLevel = 3
        )
        assertEquals(3, animInfo.abilityLevel)
    }

    @Test
    fun testJobAbilityDataSkillTiers() {
        val warriorAbilities = JobAbilityData.getAbilitiesForJob(HeroClass.WARRIOR)
        assertEquals(3, warriorAbilities.size)
        assertEquals(R.string.skill_warrior_t1_name, warriorAbilities[0].nameRes)
        assertEquals(R.string.skill_warrior_t2_name, warriorAbilities[1].nameRes)
        assertEquals(R.string.skill_warrior_t3_name, warriorAbilities[2].nameRes)
    }

    @Test
    fun testHeroLevelScalingThresholds() {
        val lowLevelHero = Hero(
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 100,
            currentMp = 20,
            level = 1,
            aiPriority = AIPriority.ATTACK
        )
        val midLevelHero = Hero(
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 200,
            currentMp = 40,
            level = 15,
            aiPriority = AIPriority.ATTACK
        )
        val highLevelHero = Hero(
            heroClass = HeroClass.WARRIOR,
            name = "Warrior",
            currentHp = 500,
            currentMp = 100,
            level = 30,
            aiPriority = AIPriority.ATTACK
        )

        fun calcLevel(hero: Hero): Int {
            val lvl = maxOf(hero.level, hero.jobMasteryLevel)
            return when {
                lvl >= 25 -> 3
                lvl >= 10 -> 2
                else -> 1
            }
        }

        assertEquals(1, calcLevel(lowLevelHero))
        assertEquals(2, calcLevel(midLevelHero))
        assertEquals(3, calcLevel(highLevelHero))
    }
}
