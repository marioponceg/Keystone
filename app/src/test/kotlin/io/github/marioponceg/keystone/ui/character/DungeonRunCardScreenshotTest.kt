package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.WithFakeImages
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.Locale
import java.util.TimeZone

/**
 * Only the expanded state gets its own goldens: the collapsed card is already inside
 * `character_detail_content_{light,dark}`, and a second capture of it would pin the same pixels
 * twice.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h600dp")
class DungeonRunCardScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var defaultLocale: Locale
    private lateinit var defaultZone: TimeZone

    @Before
    fun pinLocaleAndZone() {
        defaultLocale = Locale.getDefault()
        defaultZone = TimeZone.getDefault()
        Locale.setDefault(Locale.US)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun restoreLocaleAndZone() {
        Locale.setDefault(defaultLocale)
        TimeZone.setDefault(defaultZone)
    }

    private val run = (
        CharacterDetailStateProvider().values.toList()[1] as CharacterDetailUiState.Content
        ).profile.bestRuns.first()

    private fun capture(name: String, darkTheme: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                WithFakeImages {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DungeonRunCard(run = run, expanded = true, onToggle = {})
                    }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun dungeonRunExpandedLight() = capture("dungeon_run_expanded_light", false)

    @Test
    fun dungeonRunExpandedDark() = capture("dungeon_run_expanded_dark", true)
}
