package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.Realm
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w411dp-h1200dp")
class RealmPickerScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val cases = RealmPickerStateProvider().values.toList()

    private fun capture(name: String, darkTheme: Boolean, case: Pair<String, List<Realm>>) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                RealmPickerContent(query = case.first, results = case.second, onQueryChange = {}, onRealmSelected = {})
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun realmPickerEmptyQueryLight() = capture("realm_picker_empty_query_light", false, cases[0])

    @Test
    fun realmPickerEmptyQueryDark() = capture("realm_picker_empty_query_dark", true, cases[0])

    @Test
    fun realmPickerFilteredLight() = capture("realm_picker_filtered_light", false, cases[1])

    @Test
    fun realmPickerFilteredDark() = capture("realm_picker_filtered_dark", true, cases[1])

    @Test
    fun realmPickerNoMatchLight() = capture("realm_picker_no_match_light", false, cases[2])

    @Test
    fun realmPickerNoMatchDark() = capture("realm_picker_no_match_dark", true, cases[2])
}
