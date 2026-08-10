package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.HingeInfo
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.WithFakeImages
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import io.github.marioponceg.keystone.ui.character.CharacterDetailContent
import io.github.marioponceg.keystone.ui.character.CharacterDetailStateProvider
import io.github.marioponceg.keystone.ui.home.HomeContent
import io.github.marioponceg.keystone.ui.home.HomeStateProvider
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w1280dp-h900dp")
class KeystoneShellScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val homeContentState = HomeStateProvider().values.toList()[1]

    // Index 1 is the Content state: a loaded profile with score and best runs. (Index 0 is
    // Loading — verified against CharacterDetailStateProvider's declared value order.)
    private val detailContentState = CharacterDetailStateProvider().values.toList()[1]

    private val selectedKey = CharacterDetailKey(
        region = "EU",
        realmSlug = "silvermoon",
        realmName = "Silvermoon",
        name = "Keystone",
    )

    /**
     * Builds a [KeystoneShellState] directly, rather than through [rememberKeystoneShellState],
     * so the Home stack can be seeded with a detail entry already pushed — matching what the old
     * `rememberNavBackStack(HomeKey, selectedKey)` gave this file before the shell grew per-tab
     * stacks.
     */
    @Composable
    private fun rememberSeededShellState(withCharacter: Boolean): KeystoneShellState {
        val home = if (withCharacter) {
            rememberNavBackStack(HomeKey, selectedKey)
        } else {
            rememberNavBackStack(HomeKey)
        }
        val week = rememberNavBackStack(WeekKey)
        val profile = rememberNavBackStack(ProfileKey)
        val selected = remember { mutableStateOf(TopLevelDestination.HOME) }
        return remember(home, week, profile, selected) {
            KeystoneShellState(
                backStacks = mapOf(
                    TopLevelDestination.HOME to home,
                    TopLevelDestination.WEEK to week,
                    TopLevelDestination.PROFILE to profile,
                ),
                selectedState = selected,
            )
        }
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(withCharacter: Boolean) {
        val shellState = rememberSeededShellState(withCharacter)
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = 1280f,
                    heightDp = 900f,
                ),
                windowPosture = Posture(),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(isWidthAtLeastMedium = true, isTabletop = false),
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            weekPane = { FoundryText(text = "week") },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { _, _ ->
                CharacterDetailContent(
                    state = detailContentState,
                    onEvent = {},
                    onOpenRun = {},
                    showBackAction = false,
                )
            },
        )
    }

    private fun captureDetail(name: String, darkTheme: Boolean, withCharacter: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    TestShell(withCharacter = withCharacter)
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun shellWithCharacterLight() = captureDetail("shell_detail_light", false, withCharacter = true)

    @Test
    fun shellWithCharacterDark() = captureDetail("shell_detail_dark", true, withCharacter = true)

    @Test
    fun shellPlaceholderLight() =
        captureDetail("shell_placeholder_light", false, withCharacter = false)

    @Test
    fun shellPlaceholderDark() =
        captureDetail("shell_placeholder_dark", true, withCharacter = false)

    /**
     * A book-style foldable held open flat: a separating vertical hinge down the middle of a
     * 1280dp-wide window. Robolectric cannot produce a real FoldingFeature, so the posture is
     * built by hand and fed through the directive — which is exactly the seam KeystoneShell
     * exposes for this purpose.
     */
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun HingedShell() {
        val shellState = rememberSeededShellState(withCharacter = true)
        val density = LocalDensity.current
        val hingeCentrePx = with(density) { 640.dp.toPx() }
        val hingeHalfWidthPx = with(density) { 12.dp.toPx() }
        val windowHeightPx = with(density) { 900.dp.toPx() }
        val posture = Posture(
            isTabletop = false,
            hingeList = listOf(
                HingeInfo(
                    bounds = Rect(
                        left = hingeCentrePx - hingeHalfWidthPx,
                        top = 0f,
                        right = hingeCentrePx + hingeHalfWidthPx,
                        bottom = windowHeightPx,
                    ),
                    isFlat = true,
                    isVertical = true,
                    isSeparating = true,
                    isOccluding = true,
                ),
            ),
        )
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = 1280f,
                    heightDp = 900f,
                ),
                windowPosture = posture,
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(isWidthAtLeastMedium = true, isTabletop = false),
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            weekPane = { FoundryText(text = "week") },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { _, _ ->
                CharacterDetailContent(
                    state = detailContentState,
                    onEvent = {},
                    onOpenRun = {},
                    showBackAction = false,
                )
            },
        )
    }

    private fun captureHinged(name: String, darkTheme: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    HingedShell()
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun shellHingedLight() = captureHinged("shell_hinged_light", false)

    @Test
    fun shellHingedDark() = captureHinged("shell_hinged_dark", true)

    /**
     * The layout axis of the sparse matrix: the bar-versus-rail switch, captured with the
     * representative Home Content state behind it rather than every `UiState` variant — a state
     * does not break differently at 360dp than at 720dp; the navigation chrome does.
     */
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun LayoutShell(medium: Boolean) {
        val shellState = rememberKeystoneShellState()
        val widthDp = if (medium) MEDIUM_WIDTH_DP else COMPACT_WIDTH_DP
        val directive = calculatePaneScaffoldDirective(
            WindowAdaptiveInfo(
                windowSizeClass = WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(
                    widthDp = widthDp,
                    heightDp = WINDOW_HEIGHT_DP,
                ),
                windowPosture = Posture(),
            ),
        ).copy(horizontalPartitionSpacerSize = 0.dp)
        KeystoneShell(
            shellState = shellState,
            directive = directive,
            windowInfo = KeystoneWindowInfo(isWidthAtLeastMedium = medium, isTabletop = false),
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            weekPane = { FoundryText(text = "week") },
            profilePane = { FoundryText(text = "profile") },
            detailPane = { _, _ -> FoundryText(text = "detail") },
        )
    }

    private fun capture(name: String, darkTheme: Boolean, medium: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    LayoutShell(medium = medium)
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    @Config(qualifiers = "w411dp-h900dp")
    fun shellCompactLight() = capture("shell_compact_light", darkTheme = false, medium = false)

    @Test
    @Config(qualifiers = "w411dp-h900dp")
    fun shellCompactDark() = capture("shell_compact_dark", darkTheme = true, medium = false)

    @Test
    @Config(qualifiers = "w720dp-h900dp")
    fun shellMediumLight() = capture("shell_medium_light", darkTheme = false, medium = true)

    @Test
    @Config(qualifiers = "w720dp-h900dp")
    fun shellMediumDark() = capture("shell_medium_dark", darkTheme = true, medium = true)

    private companion object {
        const val COMPACT_WIDTH_DP = 411f
        const val MEDIUM_WIDTH_DP = 720f
        const val WINDOW_HEIGHT_DP = 900f
    }
}
