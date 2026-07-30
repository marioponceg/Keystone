package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme

/**
 * Shown in the detail pane at expanded width when no character is selected.
 *
 * Deliberately inert: auto-loading the most recent search here would fire an unrequested network
 * call on launch, which is a surprise rather than a convenience.
 */
@Composable
fun CharacterDetailPlaceholder() {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background)
            .padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(text = "⚔", style = FoundryTextStyle.Display)
        FoundryText(text = "Search for a character", style = FoundryTextStyle.Title)
        FoundryText(
            text = "Their Mythic+ score and best runs will appear here.",
            style = FoundryTextStyle.Caption,
            color = FoundryTheme.colors.onSurfaceMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailPlaceholderPreview() {
    FoundryTheme {
        CharacterDetailPlaceholder()
    }
}
