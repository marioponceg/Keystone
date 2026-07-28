package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.input.key.Key
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
        val results = listOf(
            Realm(name = "Silvermoon", slug = "silvermoon"),
            Realm(name = "Stormrage", slug = "stormrage"),
        )
        val events = mutableListOf<HomeEvent>()
        val withResults = searchableState.copy(
            isRealmSheetVisible = true,
            realmQuery = "s",
            realmResults = results,
        )
        composeRule.setContent {
            FoundryTheme {
                HomeContent(state = withResults, onEvent = { events += it })
            }
        }

        composeRule.onNodeWithTag(TAG_REALM_LIST).requestFocus()
        composeRule.onNodeWithTag(TAG_REALM_LIST).performKeyInput {
            pressKey(Key.DirectionDown)
            pressKey(Key.Enter)
        }

        assertEquals(
            HomeEvent.RealmSelected(results.first()),
            events.filterIsInstance<HomeEvent.RealmSelected>().firstOrNull(),
        )
    }
}
