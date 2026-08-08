package io.github.marioponceg.keystone.ui.common

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CustomTabTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `launches a view intent at the requested url`() {
        // Robolectric's default Instrumentation does not verify that any component resolves an
        // implicit intent - it just records whatever was passed to startActivity - so this test
        // exercises the happy path without needing a fake browser package registered.
        openInCustomTab(context, "https://raider.io/run", toolbarColor = 0xFF101010.toInt())

        val started = shadowOf(context as Application).nextStartedActivity
        assertNotNull(started)
        assertEquals(Intent.ACTION_VIEW, started.action)
        assertEquals("https://raider.io/run", started.data.toString())
    }

    @Test
    fun `a device with no browser does not start an activity`() {
        // checkActivities(true) switches the shadow to real-Android behaviour: startActivity
        // throws ActivityNotFoundException when no component resolves the intent, which is
        // exactly the "no browser installed" scenario. No component is registered to handle
        // https URLs in this test environment, so the throw happens for real - it is not
        // simulated - and openInCustomTab's catch must swallow it so nextStartedActivity is
        // still null. If the catch is removed, this test fails with an uncaught
        // ActivityNotFoundException instead of a clean assertion failure.
        shadowOf(context as Application).checkActivities(true)

        openInCustomTab(context, "https://raider.io/run", toolbarColor = 0)

        assertNull(shadowOf(context).nextStartedActivity)
    }
}
