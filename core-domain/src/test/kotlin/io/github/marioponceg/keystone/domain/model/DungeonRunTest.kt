package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class DungeonRunTest {
    @Test
    fun `isTimed is true when upgrades is positive`() {
        val run = DungeonRun(
            dungeonName = "Neltharus",
            shortName = "Nelt",
            keystoneLevel = 10,
            upgrades = 2,
            clearTime = 25.minutes,
            parTime = 36.minutes,
            score = 325.0,
            id = 1L,
            iconUrl = null,
            completedAtEpochMillis = null,
            affixes = emptyList(),
            url = null,
        )
        assertTrue(run.isTimed)
    }

    @Test
    fun `isTimed is false when upgrades is zero`() {
        val run = DungeonRun(
            dungeonName = "Neltharus",
            shortName = "Nelt",
            keystoneLevel = 10,
            upgrades = 0,
            clearTime = 40.minutes,
            parTime = 36.minutes,
            score = 200.0,
            id = 1L,
            iconUrl = null,
            completedAtEpochMillis = null,
            affixes = emptyList(),
            url = null,
        )
        assertFalse(run.isTimed)
    }

    @Test
    fun `isTimed is true when upgrades is three`() {
        val run = DungeonRun(
            dungeonName = "Neltharus",
            shortName = "Nelt",
            keystoneLevel = 10,
            upgrades = 3,
            clearTime = 20.minutes,
            parTime = 36.minutes,
            score = 375.0,
            id = 1L,
            iconUrl = null,
            completedAtEpochMillis = null,
            affixes = emptyList(),
            url = null,
        )
        assertTrue(run.isTimed)
    }
}
