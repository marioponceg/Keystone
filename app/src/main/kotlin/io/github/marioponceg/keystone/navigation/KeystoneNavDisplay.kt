package io.github.marioponceg.keystone.navigation

import androidx.activity.compose.BackHandler
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
import io.github.marioponceg.keystone.ui.adaptive.KeystoneNavigationScaffold
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import io.github.marioponceg.keystone.ui.adaptive.rememberKeystoneWindowInfo
import io.github.marioponceg.keystone.ui.character.CharacterDetailPlaceholder
import io.github.marioponceg.keystone.ui.character.CharacterDetailScreen
import io.github.marioponceg.keystone.ui.character.CharacterDetailViewModel
import io.github.marioponceg.keystone.ui.common.openInCustomTab
import io.github.marioponceg.keystone.ui.home.HomeScreen
import io.github.marioponceg.keystone.ui.profile.ProfileScreen
import io.github.marioponceg.keystone.ui.week.WeekScreen

/**
 * Production entry point: owns the shell state and wires the Hilt-backed screens into
 * [KeystoneShell], deriving the pane directive from the real window.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun KeystoneNavDisplay() {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    // The directive is what turns window size *and posture* into pane partitions, so hinge-aware
    // splitting comes from here rather than from reading FoldingFeature by hand. The spacer is
    // zeroed so the two panes meet without a gutter.
    val directive = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val windowInfo = rememberKeystoneWindowInfo()
    val shellState = rememberKeystoneShellState()
    val context = LocalContext.current
    val toolbarColor = FoundryTheme.colors.surface.toArgb()
    KeystoneShell(
        shellState = shellState,
        directive = directive,
        windowInfo = windowInfo,
        homePane = { onNavigateToCharacter ->
            HomeScreen(onNavigateToCharacter = onNavigateToCharacter)
        },
        weekPane = { WeekScreen() },
        profilePane = { onNavigateToCharacter ->
            ProfileScreen(onCharacterSelected = onNavigateToCharacter)
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
 * The adaptive shell, with the shell state, directive and pane content supplied by the caller.
 *
 * Parameterised rather than self-contained so screenshot tests can pass stateless pane content
 * (no Hilt) and a hand-built directive (a faked hinge). The same seam idiom as `openResource` in
 * `BundledRealmRepository`.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun KeystoneShell(
    shellState: KeystoneShellState,
    directive: PaneScaffoldDirective,
    windowInfo: KeystoneWindowInfo,
    homePane: @Composable ((CharacterId) -> Unit) -> Unit,
    weekPane: @Composable () -> Unit,
    profilePane: @Composable ((CharacterId) -> Unit) -> Unit,
    detailPane: @Composable (CharacterDetailKey, () -> Unit) -> Unit,
) {
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)
    // NavDisplay installs its own predictive-back handling, but only while its stack can pop. This
    // covers the other half: a non-Home tab sitting at its root, where Back must return to Home
    // rather than leave the app.
    BackHandler(enabled = shellState.canHandleBackAtRoot) {
        shellState.onBack()
    }
    KeystoneNavigationScaffold(
        selected = shellState.selected,
        onSelect = shellState::select,
        windowInfo = windowInfo,
    ) {
        NavDisplay(
            backStack = shellState.currentBackStack,
            // Routed through the shell state rather than popping the list directly, so the one Back
            // policy lives in one place — and so it stays correct if the list NavDisplay is handed
            // ever stops being the active tab's own stack.
            onBack = { shellState.onBack() },
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
                    homePane { id -> shellState.currentBackStack.pushCharacter(id) }
                }
                entry<WeekKey> { weekPane() }
                entry<ProfileKey>(
                    metadata = ListDetailSceneStrategy.listPane(
                        detailPlaceholder = { CharacterDetailPlaceholder() },
                    ),
                ) {
                    profilePane { id -> shellState.currentBackStack.pushCharacter(id) }
                }
                entry<CharacterDetailKey>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) { key ->
                    detailPane(key) { shellState.onBack() }
                }
            },
        )
    }
}

/**
 * Shared by the Home and Profile panes so the dedupe guard lives in one place: opening the
 * character already on top must be a no-op, or a double tap costs the user two presses of Back.
 */
private fun NavBackStack<NavKey>.pushCharacter(id: CharacterId) {
    val key = id.toKey()
    if (lastOrNull() != key) {
        add(key)
    }
}
