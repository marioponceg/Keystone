package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.ui.adaptive.rememberKeystoneWindowInfo
import io.github.marioponceg.keystone.ui.character.CharacterDetailPlaceholder
import io.github.marioponceg.keystone.ui.character.CharacterDetailScreen
import io.github.marioponceg.keystone.ui.character.CharacterDetailViewModel
import io.github.marioponceg.keystone.ui.common.openInCustomTab
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
    // Still needed for the tabletop posture; the back affordance deliberately no longer comes from
    // here. See below.
    val windowInfo = rememberKeystoneWindowInfo()
    val context = LocalContext.current
    val toolbarColor = FoundryTheme.colors.surface.toArgb()
    KeystoneShell(
        backStack = backStack,
        directive = directive,
        homePane = { onNavigateToCharacter ->
            HomeScreen(onNavigateToCharacter = onNavigateToCharacter)
        },
        detailPane = { key, onBack ->
            CharacterDetailScreen(
                onBack = onBack,
                // The screen takes a lambda, not a Context: keeping Intent construction here means
                // no screen composable can reach the system, and screenshot tests cannot launch
                // anything.
                onOpenRun = { url -> openInCustomTab(context, url, toolbarColor) },
                // Read off the same directive that decides the pane count, not off a second,
                // independently derived view of the window. "Detail is the only pane" and "the
                // user needs a way back to the list" are the same fact; deriving them separately
                // left nothing enforcing that they agree, and a disagreement would put a Back
                // button inside a two-pane layout where the list is already on screen.
                showBackAction = directive.maxHorizontalPartitions == 1,
                isTabletop = windowInfo.isTabletop,
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
