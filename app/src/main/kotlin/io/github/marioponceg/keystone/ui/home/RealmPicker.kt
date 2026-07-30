package io.github.marioponceg.keystone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

/** Stateless content of the realm-picker bottom sheet: a filter field over a live-filtered list. */
@Composable
fun RealmPickerContent(
    query: String,
    results: List<Realm>,
    onQueryChange: (String) -> Unit,
    onRealmSelected: (Realm) -> Unit,
) {
    val spacing = FoundryTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FoundryTheme.colors.surface) // Surface background for golden/preview fidelity.
            .padding(spacing.md),
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
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                items(results, key = { it.slug }) { realm ->
                    FoundryCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onRealmSelected(realm) },
                    ) {
                        FoundryText(text = realm.name, style = FoundryTextStyle.Body)
                    }
                }
            }
        }
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
