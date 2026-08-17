package io.github.marioponceg.keystone.ui.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryButtonStyle
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.Affix
import io.github.marioponceg.keystone.ui.common.safeDrawingContentPadding

@Composable
fun WeekScreen(viewModel: WeekViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WeekContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun WeekContent(state: WeekUiState, onEvent: (WeekEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background),
    ) {
        when (state) {
            is WeekUiState.Loading -> CentredMessage(text = "Loading this week's affixes…")
            is WeekUiState.Unavailable -> UnavailableState(onEvent = onEvent)
            is WeekUiState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = safeDrawingContentPadding(horizontal = spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                item {
                    Column(modifier = Modifier.padding(top = spacing.lg)) {
                        FoundryText(text = "This week", style = FoundryTextStyle.Display)
                        FoundryText(
                            text = state.affixes.title,
                            style = FoundryTextStyle.Body,
                            color = FoundryTheme.colors.onSurfaceMuted,
                        )
                    }
                }
                items(state.affixes.affixes, key = { it.name }) { affix ->
                    AffixRow(affix = affix)
                }
                item {
                    Spacer(modifier = Modifier.height(spacing.lg))
                }
            }
        }
    }
}

/**
 * One affix per card, name and full description. The description is already localized by
 * `GetWeeklyAffixes`; only this endpoint accepts a locale, which is why run affixes elsewhere in
 * the app are English.
 */
@Composable
private fun AffixRow(affix: Affix) {
    val spacing = FoundryTheme.spacing
    FoundryCard(modifier = Modifier.fillMaxWidth()) {
        FoundryText(text = affix.name, style = FoundryTextStyle.Heading)
        FoundryText(
            text = affix.description,
            style = FoundryTextStyle.Body,
            color = FoundryTheme.colors.onSurfaceMuted,
            modifier = Modifier.padding(top = spacing.xs),
        )
    }
}

@Composable
private fun CentredMessage(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FoundryText(text = text, style = FoundryTextStyle.Caption)
    }
}

@Composable
private fun UnavailableState(onEvent: (WeekEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "Couldn't load affixes", style = FoundryTextStyle.Title)
        FoundryButton(
            text = "Retry",
            onClick = { onEvent(WeekEvent.Retry) },
            style = FoundryButtonStyle.Tertiary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekContentPreview(
    @PreviewParameter(WeekStateProvider::class) state: WeekUiState,
) {
    FoundryTheme {
        WeekContent(state = state, onEvent = {})
    }
}
