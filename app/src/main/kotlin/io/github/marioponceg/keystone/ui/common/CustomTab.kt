package io.github.marioponceg.keystone.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
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
    try {
        intent.launchUrl(context, url.toUri())
    } catch (e: ActivityNotFoundException) {
        logger.warn("No browser available to open $url", e)
    }
}
