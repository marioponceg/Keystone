package io.github.marioponceg.keystone.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.ui.adaptive.KeystoneWindowInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * A bottom sheet spanning a desktop-width window to serve a form living in a 400dp pane is the
 * right component on a phone and the wrong one here, so it becomes a dialog. The sheet variants
 * stay covered at compact width by [RealmPickerScreenshotTest].
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w1280dp-h900dp")
class RealmPickerDialogScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    // Query and results come from the same provider that feeds the compact sheet goldens.
    // HomeStateProvider's own realmResults is empty — using it would capture "No realms match"
    // and prove nothing about the dialog.
    private val pickerState = RealmPickerStateProvider().values.toList()[0]

    // Index 1 is the Content state; copied with the picker open so the dialog is on screen.
    private val stateWithPickerOpen = HomeStateProvider().values.toList()[1].copy(
        isRealmSheetVisible = true,
        realmQuery = pickerState.first,
        realmResults = pickerState.second,
    )

    private val expandedWindow = KeystoneWindowInfo(
        isWidthAtLeastMedium = true,
        isWidthAtLeastExpanded = true,
        isTabletop = false,
    )

    private fun capture(name: String, darkTheme: Boolean) {
        composeRule.setContent {
            FoundryTheme(darkTheme = darkTheme) {
                HomeContent(
                    state = stateWithPickerOpen,
                    onEvent = {},
                    windowInfo = expandedWindow,
                )
            }
        }
        composeRule.onRoot().captureRoboImage("src/test/screenshots/$name.png")
    }

    @Test
    fun realmPickerDialogLight() = capture("realm_picker_dialog_light", false)

    @Test
    fun realmPickerDialogDark() = capture("realm_picker_dialog_dark", true)
}
