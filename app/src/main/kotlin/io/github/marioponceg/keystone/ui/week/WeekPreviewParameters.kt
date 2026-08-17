@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.marioponceg.keystone.ui.week

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.domain.model.WeeklyAffixes

// Filename follows the shared-preview-data convention rather than the single-class convention
// `MatchingDeclarationName`/`Filename` expects, matching `HomePreviewParameters.kt`.
class WeekStateProvider : PreviewParameterProvider<WeekUiState> {
    override val values = sequenceOf(
        WeekUiState.Loading,
        WeekUiState.Content(
            WeeklyAffixes(
                title = "Tyrannical, Bolstering and Raging",
                affixes = listOf(
                    Affix("Tyrannical", "Boss enemies have 30% more health and deal 15% more damage."),
                    Affix("Bolstering", "When any non-boss enemy dies, it empowers nearby allies."),
                    Affix("Raging", "Non-boss enemies enrage at 30% health, dealing 75% more damage."),
                ),
            ),
        ),
        WeekUiState.Unavailable,
    )
}
