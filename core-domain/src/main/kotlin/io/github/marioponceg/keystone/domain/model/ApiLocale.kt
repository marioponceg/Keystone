package io.github.marioponceg.keystone.domain.model

/**
 * A locale Raider.IO actually translates. The set is closed and was probed against the live API
 * on 2026-08-08; anything outside it (ja, pl, …) is answered in English, silently and without an
 * error, so there is no failure path to model — only a mapping that defaults to [EN].
 */
enum class ApiLocale(val slug: String) {
    EN("en"),
    ES("es"),
    DE("de"),
    FR("fr"),
    IT("it"),
    PT("pt"),
    RU("ru"),
    KO("ko"),
    CN("cn"),
    TW("tw"),
    ;

    companion object {
        private const val CHINESE = "zh"
        private const val TAIWAN = "tw"

        private val byLanguage = mapOf(
            "en" to EN,
            "es" to ES,
            "de" to DE,
            "fr" to FR,
            "it" to IT,
            "pt" to PT,
            "ru" to RU,
            "ko" to KO,
        )

        /**
         * Resolves the locale to request from [language] and [country], both as the platform
         * reports them. Chinese is the one case that needs the country: the API splits it into
         * simplified (`cn`) and traditional (`tw`), and the language tag alone cannot tell them
         * apart. Everything unrecognised falls back to [EN].
         */
        fun of(language: String, country: String): ApiLocale {
            val normalisedLanguage = language.lowercase()
            if (normalisedLanguage == CHINESE) {
                return if (country.lowercase() == TAIWAN) TW else CN
            }
            return byLanguage[normalisedLanguage] ?: EN
        }
    }
}
