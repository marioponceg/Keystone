package io.github.marioponceg.keystone.domain.repository

import io.github.marioponceg.keystone.domain.model.ApiLocale

/**
 * Supplies the locale to request content in. Implemented in `app`, because reading the device's
 * language is an Android concern and both core modules stay free of Android APIs.
 */
interface AppLocaleProvider {
    fun current(): ApiLocale
}
