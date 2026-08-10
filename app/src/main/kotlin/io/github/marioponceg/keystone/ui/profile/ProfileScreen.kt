package io.github.marioponceg.keystone.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.ui.common.safeDrawingContentPadding

@Composable
fun ProfileContent(state: ProfileUiState, onCharacterSelected: (CharacterId) -> Unit) {
    val spacing = FoundryTheme.spacing
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background),
    ) {
        when (state) {
            is ProfileUiState.Empty -> EmptyState()
            is ProfileUiState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = safeDrawingContentPadding(horizontal = spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                item {
                    FoundryText(
                        text = "Pinned characters",
                        style = FoundryTextStyle.Display,
                        modifier = Modifier.padding(top = spacing.lg),
                    )
                }
                items(
                    state.characters,
                    key = { "${it.region.name}/${it.realm.slug}/${it.name}" },
                ) { id ->
                    PinnedCharacterRow(id = id, onClick = { onCharacterSelected(id) })
                }
                item {
                    Spacer(modifier = Modifier.height(spacing.lg))
                }
            }
        }
    }
}

/**
 * No disabled button and no "coming soon": an empty state that explains the one action which
 * fills it is more honest than a control that does nothing.
 */
@Composable
private fun EmptyState() {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "No pinned characters", style = FoundryTextStyle.Title)
        FoundryText(
            text = "Search for a character, then pin it from its profile to keep it here.",
            style = FoundryTextStyle.Body,
            color = FoundryTheme.colors.onSurfaceMuted,
            textAlign = TextAlign.Center,
        )
    }
}

/** Mirrors `RecentSearchRow`'s hover affordance so both lists feel the same under a pointer. */
@Composable
private fun PinnedCharacterRow(id: CharacterId, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    FoundryCard(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .then(
                if (isHovered) {
                    Modifier.border(1.dp, FoundryTheme.colors.accent, FoundryTheme.shapes.lg)
                } else {
                    Modifier
                },
            ),
        onClick = onClick,
    ) {
        FoundryText(text = id.name, style = FoundryTextStyle.Heading)
        FoundryText(
            text = "${id.realm.name} (${id.region.name})",
            style = FoundryTextStyle.Caption,
            color = FoundryTheme.colors.onSurfaceMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileContentPreview(
    @PreviewParameter(ProfileStateProvider::class) state: ProfileUiState,
) {
    FoundryTheme {
        ProfileContent(state = state, onCharacterSelected = {})
    }
}
