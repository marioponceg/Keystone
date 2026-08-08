@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.marioponceg.keystone.ui.character

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.DungeonRun
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region
import io.github.marioponceg.keystone.domain.model.Role
import io.github.marioponceg.keystone.domain.model.RoleScore
import io.github.marioponceg.keystone.domain.model.RunAffix
import io.github.marioponceg.keystone.domain.model.SeasonScore
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// Filename follows Task 8's HomePreviewParameters.kt precedent (shared preview data), rather
// than the single-class convention `MatchingDeclarationName`/`Filename` expects.
class CharacterDetailStateProvider : PreviewParameterProvider<CharacterDetailUiState> {
    private val id = CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Gingi")

    private val profile = CharacterProfile(
        id = id,
        name = "Gingi",
        realm = "Tarren Mill",
        characterClass = "Druid",
        spec = "Feral",
        // Never fetched: previews go through LocalAsyncImagePreviewHandler and screenshot tests
        // through the fake loader installed by WithFakeImages.
        avatarUrl = "https://render.worldofwarcraft.com/eu/character/tarren-mill/119/preview-avatar.jpg",
        score = SeasonScore(
            overall = 3500.0,
            colorHex = "#ff8000",
            roles = listOf(
                RoleScore(Role.TANK, 2980.4, "#e268a8"),
                RoleScore(Role.DPS, 3500.0, "#ff8000"),
            ),
        ),
        bestRuns = listOf(
            DungeonRun(
                id = 14598027,
                dungeonName = "Ara-Kara, City of Echoes",
                shortName = "ARAK",
                keystoneLevel = 12,
                upgrades = 3,
                clearTime = 24.minutes + 10.seconds,
                parTime = 30.minutes,
                score = 320.5,
                iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/arakara.jpg",
                // 2026-04-18T12:00:00Z — midday, so no timezone can move it to another date.
                completedAtEpochMillis = 1776513600000,
                affixes = listOf(
                    RunAffix("Tyrannical", "https://cdn.raiderio.net/images/wow/icons/large/tyr.jpg"),
                    RunAffix("Fortified", "https://cdn.raiderio.net/images/wow/icons/large/fort.jpg"),
                ),
            ),
            DungeonRun(
                id = 14567129,
                dungeonName = "The Stonevault",
                shortName = "SV",
                keystoneLevel = 10,
                upgrades = 1,
                clearTime = 29.minutes + 45.seconds,
                parTime = 30.minutes,
                score = 285.0,
                iconUrl = "https://cdn.raiderio.net/images/wow/icons/large/stonevault.jpg",
                completedAtEpochMillis = 1776513600000,
                // Deliberately different from the first run's, so a test asserting one is closed
                // and the other open cannot pass by accident.
                affixes = listOf(
                    RunAffix("Storming", "https://cdn.raiderio.net/images/wow/icons/large/storm.jpg"),
                ),
            ),
            DungeonRun(
                id = 14512004,
                dungeonName = "City of Threads",
                shortName = "COT",
                keystoneLevel = 9,
                upgrades = 0,
                clearTime = 33.minutes,
                parTime = 30.minutes,
                score = 250.2,
                // No icon, no date, no affixes: the degraded row every golden should also show.
                iconUrl = null,
                completedAtEpochMillis = null,
                affixes = emptyList(),
            ),
        ),
    )

    override val values = sequenceOf(
        CharacterDetailUiState.Loading,
        CharacterDetailUiState.Content(profile),
        CharacterDetailUiState.NotFound(id),
        CharacterDetailUiState.Error(id),
    )
}
