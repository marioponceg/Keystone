package io.github.marioponceg.keystone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryButtonStyle
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextField
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.RecentSearch
import io.github.marioponceg.keystone.domain.model.Region

@Composable
fun HomeScreen(
    onNavigateToCharacter: (CharacterId) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToCharacter -> onNavigateToCharacter(effect.id)
            }
        }
    }
    HomeContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun HomeContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background)
            .padding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            FoundryText(
                text = "Keystone",
                style = FoundryTextStyle.Display,
                modifier = Modifier.padding(top = spacing.lg),
            )
        }
        item {
            AffixesCard(state = state.affixes, onRetry = { onEvent(HomeEvent.RetryAffixes) })
        }
        item {
            SearchForm(state = state, onEvent = onEvent)
        }
        if (state.recentSearches.isNotEmpty()) {
            item {
                FoundryText(text = "Recent searches", style = FoundryTextStyle.Heading)
            }
            items(state.recentSearches, key = { it.id }) { recent ->
                RecentSearchRow(recent = recent, onEvent = onEvent)
            }
        }
        item {
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun AffixesCard(state: AffixesUiState, onRetry: () -> Unit) {
    val spacing = FoundryTheme.spacing
    FoundryCard(modifier = Modifier.fillMaxWidth()) {
        when (state) {
            is AffixesUiState.Loading -> FoundryText(
                text = "Loading this week's affixes…",
                style = FoundryTextStyle.Caption,
            )
            is AffixesUiState.Content -> {
                FoundryText(text = state.affixes.title, style = FoundryTextStyle.Heading)
                state.affixes.affixes.forEach { affix ->
                    Column(modifier = Modifier.padding(top = spacing.sm)) {
                        FoundryText(text = affix.name, style = FoundryTextStyle.BodyStrong)
                        FoundryText(
                            text = affix.description,
                            style = FoundryTextStyle.Caption,
                            color = FoundryTheme.colors.onSurfaceMuted,
                        )
                    }
                }
            }
            is AffixesUiState.Unavailable -> {
                FoundryText(
                    text = "Couldn't load affixes",
                    style = FoundryTextStyle.Caption,
                )
                FoundryButton(
                    text = "Retry",
                    onClick = onRetry,
                    style = FoundryButtonStyle.Tertiary,
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }
        }
    }
}

@Composable
private fun SearchForm(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        FoundryTextField(
            value = state.name,
            onValueChange = { onEvent(HomeEvent.NameChanged(it)) },
            label = "Character",
            modifier = Modifier.fillMaxWidth(),
        )
        FoundryTextField(
            value = state.realm,
            onValueChange = { onEvent(HomeEvent.RealmChanged(it)) },
            label = "Realm",
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            Region.entries.forEach { region ->
                FoundryButton(
                    text = region.name,
                    onClick = { onEvent(HomeEvent.RegionSelected(region)) },
                    style = if (region == state.region) {
                        FoundryButtonStyle.Primary
                    } else {
                        FoundryButtonStyle.Secondary
                    },
                )
            }
        }
        FoundryButton(
            text = "Search",
            onClick = { onEvent(HomeEvent.SearchSubmitted) },
            enabled = state.canSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.sm),
        )
    }
}

@Composable
private fun RecentSearchRow(recent: RecentSearch, onEvent: (HomeEvent) -> Unit) {
    FoundryCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEvent(HomeEvent.RecentSelected(recent)) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FoundryText(
                text = "${recent.id.name} · ${recent.id.realm} (${recent.id.region.name})",
                style = FoundryTextStyle.Body,
            )
            FoundryText(
                text = "✕",
                style = FoundryTextStyle.Body,
                color = FoundryTheme.colors.onSurfaceMuted,
                modifier = Modifier.clickable { onEvent(HomeEvent.RecentRemoved(recent.id)) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview(
    @PreviewParameter(HomeStateProvider::class) state: HomeUiState,
) {
    FoundryTheme {
        HomeContent(state = state, onEvent = {})
    }
}
