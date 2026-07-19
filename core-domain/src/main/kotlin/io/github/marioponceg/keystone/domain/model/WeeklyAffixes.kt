package io.github.marioponceg.keystone.domain.model

/** The affix set active this week in a region. */
data class WeeklyAffixes(
    val title: String,
    val affixes: List<Affix>,
)

data class Affix(
    val name: String,
    val description: String,
)
