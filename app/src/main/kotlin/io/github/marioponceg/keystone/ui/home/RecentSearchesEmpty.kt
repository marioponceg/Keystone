package io.github.marioponceg.keystone.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme

/**
 * The two-column home's right column before the first successful search.
 *
 * Without it a fresh install at 600x600dp or larger shows a half-width form beside a blank half —
 * the default first-run view on exactly the form factors v0.2 targets. Collapsing to a single
 * column instead was rejected deliberately: the layout would then rearrange itself the first time
 * a search succeeds, moving the form out from under the user at the moment they are using it.
 *
 * Voice and construction follow `CharacterDetailPlaceholder`, the app's other "nothing here yet"
 * pane, so the two read as one app. This one is a single muted line rather than a full placeholder
 * because the "Recent searches" heading above it already names the column.
 */
@Composable
internal fun RecentSearchesEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FoundryText(
            text = "Your recent searches will appear here.",
            style = FoundryTextStyle.Caption,
            color = FoundryTheme.colors.onSurfaceMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentSearchesEmptyPreview() {
    FoundryTheme {
        RecentSearchesEmpty()
    }
}
