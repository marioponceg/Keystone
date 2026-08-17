package io.github.marioponceg.keystone.ui.adaptive

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.navigation.TopLevelDestination
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class KeystoneNavigationScaffoldTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setContent(windowInfo: KeystoneWindowInfo, onSelect: (TopLevelDestination) -> Unit = {}) {
        composeRule.setContent {
            FoundryTheme {
                KeystoneNavigationScaffold(
                    selected = TopLevelDestination.HOME,
                    onSelect = onSelect,
                    windowInfo = windowInfo,
                ) {
                    FoundryText(text = "pane")
                }
            }
        }
    }

    @Test
    fun `compact width renders a navigation bar`() {
        setContent(KeystoneWindowInfo.Compact)
        composeRule.onNodeWithTag(TAG_NAV_BAR).assertIsDisplayed()
        composeRule.onNodeWithText("pane").assertIsDisplayed()
    }

    @Test
    fun `medium width renders a navigation rail instead`() {
        setContent(KeystoneWindowInfo(isWidthAtLeastMedium = true, isTabletop = false))
        composeRule.onNodeWithTag(TAG_NAV_RAIL).assertIsDisplayed()
        composeRule.onNodeWithText("pane").assertIsDisplayed()
    }

    @Test
    fun `tapping a destination reports it`() {
        var selected: TopLevelDestination? = null
        setContent(KeystoneWindowInfo.Compact, onSelect = { selected = it })
        composeRule.onNodeWithText(TopLevelDestination.WEEK.label).performClick()
        assertEquals(TopLevelDestination.WEEK, selected)
    }
}
