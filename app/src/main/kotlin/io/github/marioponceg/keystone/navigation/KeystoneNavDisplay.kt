package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.ui.adaptive.rememberKeystoneWindowInfo
import io.github.marioponceg.keystone.ui.character.CharacterDetailPlaceholder
import io.github.marioponceg.keystone.ui.character.CharacterDetailScreen
import io.github.marioponceg.keystone.ui.character.CharacterDetailViewModel
import io.github.marioponceg.keystone.ui.home.HomeScreen

/**
 * Production entry point: wires the Hilt-backed screens into [KeystoneShell] and derives the pane
 * directive from the real window.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun KeystoneNavDisplay(backStack: NavBackStack<NavKey>) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    // The directive is what turns window size *and posture* into pane partitions, so hinge-aware
    // splitting comes from here rather than from reading FoldingFeature by hand. The spacer is
    // zeroed so the two panes meet without a gutter.
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val windowInfo = rememberKeystoneWindowInfo()
    KeystoneShell(
        backStack = backStack,
        directive = directive,
        homePane = { onNavigateToCharacter ->
            HomeScreen(onNavigateToCharacter = onNavigateToCharacter)
        },
        detailPane = { key, onBack ->
            CharacterDetailScreen(
                onBack = onBack,
                showBackAction = !windowInfo.isWidthAtLeastExpanded,
                viewModel = hiltViewModel<CharacterDetailViewModel, CharacterDetailViewModel.Factory>(
                    creationCallback = { factory -> factory.create(key) },
                ),
            )
        },
    )
}

/**
 * The adaptive shell, with the pane contents and the directive supplied by the caller.
 *
 * Parameterised rather than self-contained so screenshot tests can pass stateless pane content
 * (no Hilt) and a hand-built directive (a faked hinge). The same seam idiom as `openResource` in
 * `BundledRealmRepository`.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun KeystoneShell(
    backStack: NavBackStack<NavKey>,
    directive: PaneScaffoldDirective,
    homePane: @Composable ((CharacterId) -> Unit) -> Unit,
    detailPane: @Composable (CharacterDetailKey, () -> Unit) -> Unit,
) {
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<HomeKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { CharacterDetailPlaceholder() },
                ),
            ) {
                homePane { id ->
                    val key = id.toKey()
                    if (backStack.lastOrNull() != key) {
                        backStack.add(key)
                    }
                }
            }
            entry<CharacterDetailKey>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { key ->
                detailPane(key) { backStack.removeLastOrNull() }
            }
        },
    )
}
