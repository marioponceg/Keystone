package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.Realm
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h1200dp")
class HomeKeyboardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val searchableState = HomeStateProvider().values.toList()[1]

    @Test
    fun enterInTheNameFieldSubmitsTheSearch() {
        val events = mutableListOf<HomeEvent>()
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = searchableState, onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_NAME_FIELD).requestFocus()
        composeRule.onNodeWithTag(TAG_NAME_FIELD).performKeyInput { pressKey(Key.Enter) }

        assertTrue(
            events.contains(HomeEvent.SearchSubmitted),
            "Enter should submit; got $events",
        )
    }

    @Test
    fun enterDoesNotSubmitWhenTheFormIsIncomplete() {
        val events = mutableListOf<HomeEvent>()
        val incomplete = searchableState.copy(name = "", selectedRealm = null)
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = incomplete, onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_NAME_FIELD).requestFocus()
        composeRule.onNodeWithTag(TAG_NAME_FIELD).performKeyInput { pressKey(Key.Enter) }

        assertTrue(
            !events.contains(HomeEvent.SearchSubmitted),
            "Enter must not submit an incomplete form; got $events",
        )
    }

    @Test
    fun escapeDismissesTheRealmPicker() {
        val events = mutableListOf<HomeEvent>()
        val withPickerOpen = searchableState.copy(isRealmSheetVisible = true)
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = withPickerOpen, onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_REALM_LIST).requestFocus()
        composeRule.onNodeWithTag(TAG_REALM_LIST).performKeyInput { pressKey(Key.Escape) }

        assertTrue(
            events.contains(HomeEvent.RealmSheetDismissed),
            "Escape should dismiss the picker; got $events",
        )
    }

    @Test
    fun arrowDownThenEnterSelectsTheFirstRealm() {
        val events = mutableListOf<HomeEvent>()
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = stateWithRealms(), onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_REALM_LIST).requestFocus()
        composeRule.onNodeWithTag(TAG_REALM_LIST).performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }

        assertEquals(
            HomeEvent.RealmSelected(realms.first()),
            events.filterIsInstance<HomeEvent.RealmSelected>().firstOrNull(),
        )
    }

    /**
     * The other keyboard tests all call [requestFocus] on the list first, which is the one step a
     * real user never performs — the picker is opened by clicking the trigger, so focus stays on
     * the trigger unless the picker claims it. This test deliberately omits that call, so it fails
     * unless the picker requests focus for itself when it opens.
     */
    @Test
    fun theRealmListTakesFocusWhenThePickerOpens() {
        val events = mutableListOf<HomeEvent>()
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = stateWithRealms(), onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_REALM_LIST).assertIsFocused()
        composeRule.onNodeWithTag(TAG_REALM_LIST).performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }

        assertEquals(
            HomeEvent.RealmSelected(realms.first()),
            events.filterIsInstance<HomeEvent.RealmSelected>().firstOrNull(),
            "Arrow keys must work without tabbing into the list first; got $events",
        )
    }

    /**
     * Up-arrow on a freshly opened list must not highlight a row: coercing `-1 - 1` up to `0` would
     * make Up and Down both land on the first result, so Enter would then select it.
     */
    @Test
    fun arrowUpDoesNothingWhenNoRealmIsHighlighted() {
        val events = mutableListOf<HomeEvent>()
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = stateWithRealms(), onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_REALM_LIST).requestFocus()
        composeRule.onNodeWithTag(TAG_REALM_LIST).performKeyInput {
            pressKey(Key.DirectionUp)
            pressKey(Key.Enter)
        }

        assertTrue(
            events.filterIsInstance<HomeEvent.RealmSelected>().isEmpty(),
            "Up from nothing highlighted must not select a realm; got $events",
        )
    }

    private val realms = listOf(
        Realm(name = "Silvermoon", slug = "silvermoon"),
        Realm(name = "Stormrage", slug = "stormrage"),
    )

    private fun stateWithRealms() = searchableState.copy(
        isRealmSheetVisible = true,
        realmQuery = "s",
        realmResults = realms,
    )
}
