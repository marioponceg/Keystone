package io.github.marioponceg.keystone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextField
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo

/**
 * Stateless content of the realm picker: a filter field over a live-filtered list.
 *
 * Keyboard navigation is a genuine requirement rather than polish — the realm lists run to several
 * hundred entries per region, so reaching one by pointer alone is slow on a desktop window.
 */
@Composable
fun RealmPickerContent(
    query: String,
    results: List<Realm>,
    onQueryChange: (String) -> Unit,
    onRealmSelected: (Realm) -> Unit,
    onDismiss: () -> Unit = {},
) {
    val spacing = FoundryTheme.spacing
    val listState = rememberLazyListState()
    // Reset on every new result list: index 2 of the old results means nothing in the new ones.
    // -1 is "nothing highlighted", so the picker opens with no row pre-selected.
    var highlighted by remember(results) { mutableIntStateOf(-1) }

    LaunchedEffect(highlighted) {
        if (highlighted >= 0) {
            listState.animateScrollToItem(highlighted)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FoundryTheme.colors.surface) // Surface background for golden/preview fidelity.
            .padding(spacing.md)
            .testTag(TAG_REALM_LIST)
            .focusable()
            .onKeyEvent { event ->
                when (event.toRealmListCommand()) {
                    RealmListCommand.Next ->
                        if (results.isNotEmpty()) {
                            highlighted = (highlighted + 1).coerceAtMost(results.lastIndex)
                        }
                    RealmListCommand.Previous ->
                        if (results.isNotEmpty()) {
                            highlighted = (highlighted - 1).coerceAtLeast(0)
                        }
                    RealmListCommand.Confirm -> results.getOrNull(highlighted)?.let(onRealmSelected)
                    RealmListCommand.Dismiss -> onDismiss()
                    null -> return@onKeyEvent false
                }
                true
            },
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FoundryText(text = "Select realm", style = FoundryTextStyle.Heading)
        FoundryTextField(
            value = query,
            onValueChange = onQueryChange,
            label = "Search realms",
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (results.isEmpty()) {
            FoundryText(
                text = "No realms match",
                style = FoundryTextStyle.Caption,
                color = FoundryTheme.colors.onSurfaceMuted,
            )
        } else {
            RealmResults(
                results = results,
                listState = listState,
                highlighted = highlighted,
                onRealmSelected = onRealmSelected,
            )
        }
    }
}

@Composable
private fun RealmResults(
    results: List<Realm>,
    listState: LazyListState,
    highlighted: Int,
    onRealmSelected: (Realm) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.heightIn(max = 360.dp),
        verticalArrangement = Arrangement.spacedBy(FoundryTheme.spacing.xs),
    ) {
        itemsIndexed(results, key = { _, realm -> realm.slug }) { index, realm ->
            RealmRow(
                realm = realm,
                isHighlighted = index == highlighted,
                onClick = { onRealmSelected(realm) },
            )
        }
    }
}

/**
 * What the realm list does with a key press. Mapping the raw event to an intent first keeps the
 * `onKeyEvent` lambda down to the state changes it actually makes.
 */
private enum class RealmListCommand { Next, Previous, Confirm, Dismiss }

/** `null` means "not ours" — the event falls through to the platform unconsumed. */
private fun KeyEvent.toRealmListCommand(): RealmListCommand? {
    if (type != KeyEventType.KeyDown) return null
    return when (key) {
        Key.DirectionDown -> RealmListCommand.Next
        Key.DirectionUp -> RealmListCommand.Previous
        Key.Enter -> RealmListCommand.Confirm
        Key.Escape -> RealmListCommand.Dismiss
        else -> null
    }
}

/**
 * The row rendering is unchanged from the pointer-only version — only the arrow-key highlight is
 * new, and it is absent until a key is pressed, so the committed goldens stay valid.
 *
 * The border repeats [FoundryCard]'s own shape: with `border`'s default `RectangleShape` the
 * highlight would be a square drawn around rounded corners.
 */
@Composable
private fun RealmRow(realm: Realm, isHighlighted: Boolean, onClick: () -> Unit) {
    FoundryCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isHighlighted) {
                    Modifier.border(2.dp, FoundryTheme.colors.accent, FoundryTheme.shapes.lg)
                } else {
                    Modifier
                },
            ),
        onClick = onClick,
    ) {
        FoundryText(text = realm.name, style = FoundryTextStyle.Body)
    }
}

/**
 * The picker's container is the only thing that varies with window size — [HomeUiState] keeps
 * `isRealmSheetVisible` with exactly its previous meaning, so the ViewModel and its tests are
 * untouched.
 *
 * A dialog is chosen on window width rather than on available pane width: a dialog floats above
 * the whole window, so the window is the right thing to measure even when the form that opened it
 * sits in a narrow pane.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RealmPickerContainer(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    windowInfo: KeystoneWindowInfo,
) {
    val picker: @Composable () -> Unit = {
        RealmPickerContent(
            query = state.realmQuery,
            results = state.realmResults,
            onQueryChange = { onEvent(HomeEvent.RealmQueryChanged(it)) },
            onRealmSelected = { onEvent(HomeEvent.RealmSelected(it)) },
            onDismiss = { onEvent(HomeEvent.RealmSheetDismissed) },
        )
    }
    if (windowInfo.isWidthAtLeastMedium) {
        Dialog(onDismissRequest = { onEvent(HomeEvent.RealmSheetDismissed) }) {
            FoundryCard(modifier = Modifier.widthIn(max = 480.dp)) {
                picker()
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = { onEvent(HomeEvent.RealmSheetDismissed) },
            containerColor = FoundryTheme.colors.surface,
        ) {
            picker()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RealmPickerContentPreview(
    @PreviewParameter(RealmPickerStateProvider::class) state: Pair<String, List<Realm>>,
) {
    FoundryTheme {
        RealmPickerContent(
            query = state.first,
            results = state.second,
            onQueryChange = {},
            onRealmSelected = {},
        )
    }
}
