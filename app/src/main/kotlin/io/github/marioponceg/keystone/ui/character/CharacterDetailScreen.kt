package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun CharacterDetailScreen(
    onBack: () -> Unit,
    viewModel: CharacterDetailViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CharacterDetailContent(
        state = state,
        onEvent = { event ->
            when (event) {
                CharacterDetailEvent.Back -> onBack()
                else -> viewModel.onEvent(event)
            }
        },
    )
}

@Composable
fun CharacterDetailContent(state: CharacterDetailUiState, onEvent: (CharacterDetailEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background),
    ) {
        when (state) {
            is CharacterDetailUiState.Loading -> LoadingState()
            is CharacterDetailUiState.NotFound -> NotFoundState(id = state.id, onEvent = onEvent)
            is CharacterDetailUiState.Error -> ErrorState(onEvent = onEvent)
            is CharacterDetailUiState.Content -> ContentState(profile = state.profile)
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
private fun NotFoundState(id: CharacterId, onEvent: (CharacterDetailEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "Character not found", style = FoundryTextStyle.Title)
        FoundryText(
            text = "${id.name} — ${id.realm.name} (${id.region.name})",
            color = FoundryTheme.colors.onSurfaceMuted,
        )
        FoundryButton(
            text = "Back",
            onClick = { onEvent(CharacterDetailEvent.Back) },
            modifier = Modifier.padding(top = spacing.sm),
        )
    }
}

@Composable
private fun ErrorState(onEvent: (CharacterDetailEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.md),
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

@Preview(showBackground = true)
@Composable
private fun CharacterDetailContentPreview(
    @PreviewParameter(CharacterDetailStateProvider::class) state: CharacterDetailUiState,
) {
    FoundryTheme {
        CharacterDetailContent(state = state, onEvent = {})
    }
}
