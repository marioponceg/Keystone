package io.github.marioponceg.keystone.ui.character

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h1200dp")
class CharacterDetailExpansionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val contentState = CharacterDetailStateProvider().values.toList()[1]

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

    private fun setContent() {
        composeRule.setContent {
            FoundryTheme {
                WithFakeImages {
                    CharacterDetailContent(state = contentState, onEvent = {})
                }
            }
        }
    }

    @Test
    fun `tapping a run reveals its affixes`() {
        setContent()
        composeRule.onNodeWithText("Tyrannical").assertDoesNotExist()

        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()

        composeRule.onNodeWithText("Tyrannical").assertIsDisplayed()
    }

    @Test
    fun `tapping the same run again collapses it`() {
        setContent()
        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()
        composeRule.onNodeWithText("Tyrannical").assertIsDisplayed()

        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()

        composeRule.onNodeWithText("Tyrannical").assertDoesNotExist()
    }

    @Test
    fun `opening a second run closes the first`() {
        setContent()
        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()

        composeRule.onNodeWithText("The Stonevault").performClick()

        composeRule.onNodeWithText("Storming").assertIsDisplayed()
        composeRule.onNodeWithText("Tyrannical").assertDoesNotExist()
    }

    @Test
    fun `an expanded run shows its completion date`() {
        setContent()
        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()

        composeRule.onNodeWithText("Apr 18, 2026").assertIsDisplayed()
    }
}
