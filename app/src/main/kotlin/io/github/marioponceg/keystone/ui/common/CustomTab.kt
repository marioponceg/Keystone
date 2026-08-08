package io.github.marioponceg.keystone.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import io.github.marioponceg.quill.Quill

private val logger = Quill.logger("CustomTab")

/**
 * Opens [url] in a Custom Tab themed to match the app, so the hand-off does not flash a foreign
 * white toolbar.
 *
 * A device with no browser at all is rare but real — bare emulators, some ROMs — and
 * `launchUrl` throws `ActivityNotFoundException` there. The app has no snackbar infrastructure and
 * this is not the feature that should invent one, so the failure is logged and the tap does
 * nothing visible.
 */
fun openInCustomTab(context: Context, url: String, toolbarColor: Int) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setDefaultColorSchemeParams(
            CustomTabColorSchemeParams.Builder()
                .setToolbarColor(toolbarColor)
                .build(),
        )
        .build()
    // The caller here is `KeystoneNavDisplay`, which is composed with an Activity context, so
    // this flag is not strictly required in production. It is required in this Robolectric suite
    // (an application context via `ApplicationProvider`), and it is harmless from an Activity, so
    // it is applied unconditionally rather than only in tests.
    intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        intent.launchUrl(context, url.toUri())
    } catch (e: ActivityNotFoundException) {
        logger.warn("No browser available to open $url", e)
    }
}
