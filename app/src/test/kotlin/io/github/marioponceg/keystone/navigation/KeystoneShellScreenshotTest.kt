package io.github.marioponceg.keystone.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.HingeInfo
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.WithFakeImages
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

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun TestShell(withCharacter: Boolean) {
        val backStack = if (withCharacter) {
            rememberNavBackStack(HomeKey, selectedKey)
        } else {
            rememberNavBackStack(HomeKey)
        }
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
            backStack = backStack,
            directive = directive,
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            detailPane = { _, _ ->
                CharacterDetailContent(
                    state = detailContentState,
                    onEvent = {},
                    showBackAction = false,
                )
            },
        )
    }

    private fun capture(name: String, darkTheme: Boolean, withCharacter: Boolean) {
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
    fun shellWithCharacterLight() = capture("shell_detail_light", false, withCharacter = true)

    @Test
    fun shellWithCharacterDark() = capture("shell_detail_dark", true, withCharacter = true)

    @Test
    fun shellPlaceholderLight() = capture("shell_placeholder_light", false, withCharacter = false)

    @Test
    fun shellPlaceholderDark() = capture("shell_placeholder_dark", true, withCharacter = false)

    /**
     * A book-style foldable held open flat: a separating vertical hinge down the middle of a
     * 1280dp-wide window. Robolectric cannot produce a real FoldingFeature, so the posture is
     * built by hand and fed through the directive — which is exactly the seam KeystoneShell
     * exposes for this purpose.
     */
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    private fun HingedShell() {
        val backStack = rememberNavBackStack(HomeKey, selectedKey)
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
            backStack = backStack,
            directive = directive,
            homePane = { HomeContent(state = homeContentState, onEvent = {}) },
            detailPane = { _, _ ->
                CharacterDetailContent(
                    state = detailContentState,
                    onEvent = {},
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
}
