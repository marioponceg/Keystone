package io.github.marioponceg.keystone.ui.character

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.marioponceg.foundry.components.FoundryButton
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.ui.common.safeDrawingContentPadding

@Composable
fun CharacterDetailScreen(
    onBack: () -> Unit,
    viewModel: CharacterDetailViewModel,
    showBackAction: Boolean = true,
    isTabletop: Boolean = false,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterDetailContent(
        state = state,
        showBackAction = showBackAction,
        isTabletop = isTabletop,
        onEvent = { event ->
            when (event) {
                CharacterDetailEvent.Back -> onBack()
                else -> viewModel.onEvent(event)
            }
        },
    )
}

@Composable
fun CharacterDetailContent(
    state: CharacterDetailUiState,
    onEvent: (CharacterDetailEvent) -> Unit,
    showBackAction: Boolean = true,
    isTabletop: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background),
    ) {
        when (state) {
            is CharacterDetailUiState.Loading -> LoadingState()
            is CharacterDetailUiState.NotFound -> NotFoundState(
                id = state.id,
                onEvent = onEvent,
                showBackAction = showBackAction,
            )
            is CharacterDetailUiState.Error -> ErrorState(onEvent = onEvent)
            is CharacterDetailUiState.Content ->
                if (isTabletop) {
                    TabletopContentState(profile = state.profile)
                } else {
                    ContentState(profile = state.profile)
                }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FoundryText(text = "Loading…", style = FoundryTextStyle.Caption)
    }
}

@Composable
private fun NotFoundState(
    id: CharacterId,
    onEvent: (CharacterDetailEvent) -> Unit,
    showBackAction: Boolean,
) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            // The Back button below must stay clear of the navigation bar to remain tappable.
            .safeDrawingPadding()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "Character not found", style = FoundryTextStyle.Title)
        FoundryText(
            text = "${id.name} — ${id.realm.name} (${id.region.name})",
            color = FoundryTheme.colors.onSurfaceMuted,
        )
        // A detail pane inside a list-detail layout must not offer "back": the list is already on
        // screen next to it, so there is nowhere to go back to.
        if (showBackAction) {
            FoundryButton(
                text = "Back",
                onClick = { onEvent(CharacterDetailEvent.Back) },
                modifier = Modifier.padding(top = spacing.sm),
            )
        }
    }
}

@Composable
private fun ErrorState(onEvent: (CharacterDetailEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "Something went wrong", style = FoundryTextStyle.Title)
        FoundryButton(
            text = "Retry",
            onClick = { onEvent(CharacterDetailEvent.Retry) },
            modifier = Modifier.padding(top = spacing.sm),
        )
    }
}

@Composable
private fun ContentState(profile: CharacterProfile) {
    val spacing = FoundryTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = safeDrawingContentPadding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            Header(profile = profile, modifier = Modifier.padding(top = spacing.lg))
        }
        item {
            ScoreCard(score = profile.score)
        }
        item {
            FoundryText(text = "Best runs", style = FoundryTextStyle.Heading)
        }
        items(profile.bestRuns, key = { it.dungeonName }) { run ->
            DungeonRunCard(run = run)
        }
        item {
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

/**
 * Tabletop split: identity and score occupy the top half (above the horizontal fold, facing the
 * user) while the scrollable runs list takes the bottom half. A 50/50 split rather than a measured
 * hinge offset — a half-open foldable folds at its midpoint, and hard-coding the ratio keeps this
 * free of window-metrics plumbing that would not be testable in Robolectric anyway.
 */
@Composable
private fun TabletopContentState(profile: CharacterProfile) {
    val spacing = FoundryTheme.spacing
    Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
        ) {
            Header(profile = profile)
            ScoreCard(score = profile.score)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            FoundryText(text = "Best runs", style = FoundryTextStyle.Heading)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                items(profile.bestRuns, key = { it.dungeonName }) { run ->
                    DungeonRunCard(run = run)
                }
                item {
                    Spacer(modifier = Modifier.height(spacing.lg))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailContentPreview(
    @PreviewParameter(CharacterDetailStateProvider::class) state: CharacterDetailUiState,
) {
    FoundryTheme {
        CharacterDetailContent(state = state, onEvent = {})
    }
}
