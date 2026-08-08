package io.github.marioponceg.keystone.locale

import androidx.core.os.LocaleListCompat
import io.github.marioponceg.keystone.domain.model.ApiLocale
import io.github.marioponceg.keystone.domain.repository.AppLocaleProvider
import java.util.Locale

/**
 * Resolves the locale from the device.
 *
 * [locales] is a function rather than a value so the locale is re-read on every call: the user can
 * change the language while the app is alive, and a captured value would go stale.
 *
 * The default reads the *effective app locale* via [LocaleListCompat.getDefault], which honours a
 * per-app language override (Android 13+) instead of jumping straight to the system default.
 */
class DeviceLocaleProvider(private val locales: () -> Locale) : AppLocaleProvider {

    constructor() : this({
        LocaleListCompat.getDefault().get(0) ?: Locale.getDefault()
    })

    override fun current(): ApiLocale {
        val locale = locales()
        return ApiLocale.of(language = locale.language, country = locale.country)
    }
}
