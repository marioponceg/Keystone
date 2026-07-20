@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

// Filename follows Task 8's "HomePreviewParameters.kt" spec (shared preview data), rather than
// the single-class convention `MatchingDeclarationName`/`Filename` expects.
class HomeStateProvider : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(
        HomeUiState(),
        HomeUiState(
            affixes = AffixesUiState.Content(
                WeeklyAffixes(
                    title = "Tyrannical, Bolstering and Raging",
                    affixes = listOf(
                        Affix("Tyrannical", "Boss enemies have 30% more health and deal 15% more damage."),
                        Affix("Bolstering", "When any non-boss enemy dies, it empowers nearby allies."),
                    ),
                ),
            ),
            name = "Gingi",
            realm = "Tarren Mill",
            recentSearches = listOf(
                RecentSearch(CharacterId(Region.EU, "Tarren Mill", "Gingi"), 1),
                RecentSearch(CharacterId(Region.US, "Illidan", "Dorki"), 2),
            ),
        ),
        HomeUiState(affixes = AffixesUiState.Unavailable),
    )
}
