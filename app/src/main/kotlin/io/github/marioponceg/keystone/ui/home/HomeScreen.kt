package io.github.marioponceg.keystone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import io.github.marioponceg.keystone.ui.adaptive.rememberKeystoneWindowInfo

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
    HomeContent(
        state = state,
        onEvent = viewModel::onEvent,
        windowInfo = rememberKeystoneWindowInfo(),
    )
}

/**
 * Below this width Home is a single scrolling column; at or above it, the search form and recents
 * sit side by side under a full-width affixes card.
 *
 * Measured against the width this composable is *given*, not the window width: at expanded width
 * Home renders inside a ~400dp list pane of a 1280dp window, and must stay single-column there.
 */
internal const val HOME_TWO_COLUMN_MIN_WIDTH_DP = 600

/**
 * Two columns also need vertical room. Below this height the single-column layout wins even when
 * the window is wide: the two-column layout does not scroll as a whole, so in a short window
 * (split screen, a half-open foldable, a resized desktop window) the title and affixes card alone
 * could push the form out of reach. A 400dp-tall window should not pretend to be a tablet.
 */
internal const val HOME_TWO_COLUMN_MIN_HEIGHT_DP = 600

internal const val TAG_NAME_FIELD = "home_name_field"
internal const val TAG_REALM_LIST = "realm_picker_list"
internal const val TAG_REALM_FILTER = "realm_picker_filter"

@Composable
fun HomeContent(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    windowInfo: KeystoneWindowInfo = KeystoneWindowInfo.Compact,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(FoundryTheme.colors.background),
    ) {
        if (maxWidth >= HOME_TWO_COLUMN_MIN_WIDTH_DP.dp &&
            maxHeight >= HOME_TWO_COLUMN_MIN_HEIGHT_DP.dp
        ) {
            HomeTwoColumnContent(state = state, onEvent = onEvent)
        } else {
            HomeSingleColumnContent(state = state, onEvent = onEvent)
        }
    }
    if (state.isRealmSheetVisible) {
        RealmPickerContainer(state = state, onEvent = onEvent, windowInfo = windowInfo)
    }
}

@Composable
private fun HomeSingleColumnContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
            recentSearchItems(recents = state.recentSearches, onEvent = onEvent)
        }
        item {
            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

/**
 * Shared by both layouts so the key derivation lives in exactly one place.
 *
 * The key must be Bundle-storable and `CharacterId` is not, so it is flattened to a string.
 */
private fun LazyListScope.recentSearchItems(
    recents: List<RecentSearch>,
    onEvent: (HomeEvent) -> Unit,
) {
    items(recents, key = { "${it.id.region.name}/${it.id.realm.slug}/${it.id.name}" }) { recent ->
        RecentSearchRow(recent = recent, onEvent = onEvent)
    }
}

@Composable
private fun HomeTwoColumnContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FoundryText(
            text = "Keystone",
            style = FoundryTextStyle.Display,
            modifier = Modifier.padding(top = spacing.lg),
        )
        // Affixes span the full width: three affixes in a row is content that improves with width,
        // unlike a text field, which does not.
        AffixesCard(state = state.affixes, onRetry = { onEvent(HomeEvent.RetryAffixes) })
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Takes the height left over after the title and affixes so neither column can
                // overflow the window: each scrolls inside its own bounded space.
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                SearchForm(state = state, onEvent = onEvent)
            }
            // The heading is unconditional so the column keeps its identity from first run: with
            // the two-column layout chosen on window size alone, an empty recents list still owns
            // half the window, and a blank half is the default first-run view without this.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                FoundryText(text = "Recent searches", style = FoundryTextStyle.Heading)
                if (state.recentSearches.isEmpty()) {
                    RecentSearchesEmpty(modifier = Modifier.weight(1f))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        recentSearchItems(recents = state.recentSearches, onEvent = onEvent)
                    }
                }
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TAG_NAME_FIELD)
                // Enter submits only when the form is complete, matching the Search button's
                // enabled state — a keyboard user must not be able to trigger what a pointer user
                // cannot.
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == Key.Enter &&
                        state.canSearch
                    ) {
                        onEvent(HomeEvent.SearchSubmitted)
                        true
                    } else {
                        false
                    }
                },
        )
        RealmTrigger(realmName = state.selectedRealm?.name, onClick = { onEvent(HomeEvent.RealmFieldTapped) })
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

/** Tappable trigger standing in for the realm field; opens the realm-picker sheet. */
@Composable
private fun RealmTrigger(realmName: String?, onClick: () -> Unit) {
    val spacing = FoundryTheme.spacing
    FoundryCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column {
            FoundryText(
                text = "Realm",
                style = FoundryTextStyle.Caption,
                color = FoundryTheme.colors.onSurfaceMuted,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                FoundryText(
                    text = realmName ?: "Select realm",
                    style = FoundryTextStyle.Body,
                    color = if (realmName == null) FoundryTheme.colors.onSurfaceMuted else Color.Unspecified,
                    modifier = Modifier.weight(1f),
                )
                FoundryText(
                    text = "▾",
                    style = FoundryTextStyle.Body,
                    color = FoundryTheme.colors.onSurfaceMuted,
                )
            }
        }
    }
}

/**
 * The hover border is the pointer counterpart of the keyboard highlight: on a desktop window a row
 * that never reacts to the cursor reads as inert. It costs nothing on touch, where hover never
 * fires.
 *
 * The border repeats [FoundryCard]'s own shape; `border`'s default is a rectangle, which would cut
 * the corners off.
 */
@Composable
private fun RecentSearchRow(recent: RecentSearch, onEvent: (HomeEvent) -> Unit) {
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
        onClick = { onEvent(HomeEvent.RecentSelected(recent)) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FoundryText(
                text = "${recent.id.name} · ${recent.id.realm.name} (${recent.id.region.name})",
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
