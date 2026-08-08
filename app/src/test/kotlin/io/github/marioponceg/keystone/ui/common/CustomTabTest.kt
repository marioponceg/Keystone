package io.github.marioponceg.keystone.ui.common

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CustomTabTest {

    // Production always calls `openInCustomTab` with `LocalContext.current` from
    // `KeystoneNavDisplay`, which is an Activity context, not an Application one. An
    // Activity-backed Robolectric context is used here so the test exercises the same
    // startActivity path production does, instead of the Application-context path that
    // required `FLAG_ACTIVITY_NEW_TASK` purely to satisfy the test harness.
    private val activity: ComponentActivity =
        Robolectric.buildActivity(ComponentActivity::class.java).setup().get()

    @Test
    fun `launches a view intent at the requested url`() {
        // Robolectric's default Instrumentation does not verify that any component resolves an
        // implicit intent - it just records whatever was passed to startActivity - so this test
        // exercises the happy path without needing a fake browser package registered.
        openInCustomTab(activity, "https://raider.io/run", toolbarColor = 0xFF101010.toInt())

        val started = shadowOf(activity).nextStartedActivity
        assertNotNull(started)
        assertEquals(Intent.ACTION_VIEW, started.action)
        assertEquals("https://raider.io/run", started.data.toString())

        // FLAG_ACTIVITY_NEW_TASK must never come back. Launching from an Activity context with
        // that flag set makes Android bring the browser's existing task to the foreground and
        // stack the Custom Tab on top of it, so Back walks the browser's history instead of
        // returning to Keystone. If a future change moves this test back to an Application
        // context, Robolectric will throw `AndroidRuntimeException: Calling startActivity() from
        // outside of an Activity context requires FLAG_ACTIVITY_NEW_TASK` here - do not silence
        // that by re-adding the flag to production; fix the test context instead.
        assertEquals(0, started.flags and Intent.FLAG_ACTIVITY_NEW_TASK)

        // setDefaultColorSchemeParams(...) packs the toolbar color into the intent extras under
        // this same key (verified against the androidx.browser 1.10.0 sources: `toBundle()` in
        // `CustomTabColorSchemeParams` calls `bundle.putInt(EXTRA_TOOLBAR_COLOR, toolbarColor)`,
        // and `CustomTabsIntent.Builder.build()` merges that bundle straight into the intent's
        // extras). Deleting the `setDefaultColorSchemeParams` call would leave the Custom Tab
        // themed with the browser's default colors instead of Keystone's, with no golden able to
        // catch it.
        assertEquals(
            0xFF101010.toInt(),
            started.getIntExtra(CustomTabsIntent.EXTRA_TOOLBAR_COLOR, 0),
        )
    }

    @Test
    fun `a device with no browser does not start an activity`() {
        // checkActivities(true) switches the shadow to real-Android behaviour: startActivity
        // throws ActivityNotFoundException when no component resolves the intent, which is
        // exactly the "no browser installed" scenario. This flag lives on the process-wide
        // shadow Instrumentation (reachable from either an Application or Activity shadow), so
        // setting it here also governs the Activity context used below. No component is
        // registered to handle https URLs in this test environment, so the throw happens for
        // real - it is not simulated - and openInCustomTab's catch must swallow it so
        // nextStartedActivity is still null. If the catch is removed, this test fails with an
        // uncaught ActivityNotFoundException instead of a clean assertion failure.
        shadowOf(activity.application).checkActivities(true)

        openInCustomTab(activity, "https://raider.io/run", toolbarColor = 0)

        assertNull(shadowOf(activity).nextStartedActivity)
    }
}
