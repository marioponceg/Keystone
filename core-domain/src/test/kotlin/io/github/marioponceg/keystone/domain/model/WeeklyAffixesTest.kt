package io.github.marioponceg.keystone.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class WeeklyAffixesTest {
    @Test
    fun `WeeklyAffixes stores title`() {
        val affixes = WeeklyAffixes(
            title = "Week 1",
            affixes = listOf(Affix("Bolstering", "Enemies have more health"))
        )
        assertEquals("Week 1", affixes.title)
    }

    @Test
    fun `WeeklyAffixes stores affixes list`() {
        val affix = Affix("Bolstering", "Enemies have more health")
        val affixes = WeeklyAffixes("Week 1", listOf(affix))
        assertEquals(listOf(affix), affixes.affixes)
    }
}

class AffixTest {
    @Test
    fun `Affix stores name`() {
        val affix = Affix("Bolstering", "Enemies have more health")
        assertEquals("Bolstering", affix.name)
    }

    @Test
    fun `Affix stores description`() {
        val affix = Affix("Bolstering", "Enemies have more health")
        assertEquals("Enemies have more health", affix.description)
    }
}
