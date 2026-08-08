package io.github.marioponceg.keystone.ui.character

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.height
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
import kotlin.test.assertEquals

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

    private fun setContent(onOpenRun: (String) -> Unit = {}) {
        composeRule.setContent {
            FoundryTheme {
                WithFakeImages {
                    CharacterDetailContent(
                        state = contentState,
                        onEvent = {},
                        onOpenRun = onOpenRun,
                    )
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

    @Test
    fun `the link button reports the run's own url`() {
        val opened = mutableListOf<String>()
        setContent(onOpenRun = { opened += it })

        composeRule.onNodeWithText("Ara-Kara, City of Echoes").performClick()
        composeRule.onNodeWithText("View on Raider.IO").performClick()

        assertEquals(listOf("https://raider.io/mythic-plus-runs/season-mn-1/14598027-12-ara-kara"), opened)
    }

    @Test
    fun `a run with no url shows no link button`() {
        setContent()

        composeRule.onNodeWithText("City of Threads").performClick()

        composeRule.onNodeWithText("View on Raider.IO").assertDoesNotExist()
    }

    // City of Threads has no date, no affixes and no url — the only run with nothing at all to
    // show once expanded. Before this task, `ExpandedRunDetails` rendered an unconditional
    // `Column` with top padding regardless of content, so this run grew a few dp on expansion for
    // nothing. FoundryCard's onClick makes it a merged semantics node, so `onNodeWithText` here
    // (the same query the other tests in this file already click through) resolves to the whole
    // card, not just the label — its bounds capture the card's full height, panel included.
    // Removing the "nothing to show" guard reintroduces the padding-only Column and grows that
    // height on expansion, even though no visible text changes — a presence/absence check on text
    // alone cannot catch that regression.
    @Test
    fun `a run with nothing to show adds no panel when expanded`() {
        setContent()
        val card = composeRule.onNodeWithText("City of Threads")
        val collapsedHeight = card.getUnclippedBoundsInRoot().height

        card.performClick()

        val expandedHeight = card.getUnclippedBoundsInRoot().height
        assertEquals(collapsedHeight, expandedHeight)
    }
}
