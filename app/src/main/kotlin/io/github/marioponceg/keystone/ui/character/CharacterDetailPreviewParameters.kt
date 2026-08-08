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
                dungeonName = "Ara-Kara, City of Echoes",
                shortName = "ARAK",
                keystoneLevel = 12,
                upgrades = 3,
                clearTime = 24.minutes + 10.seconds,
                parTime = 30.minutes,
                score = 320.5,
                id = 1L,
                iconUrl = null,
                completedAtEpochMillis = null,
                affixes = emptyList(),
            ),
            DungeonRun(
                dungeonName = "The Stonevault",
                shortName = "SV",
                keystoneLevel = 10,
                upgrades = 1,
                clearTime = 29.minutes + 45.seconds,
                parTime = 30.minutes,
                score = 285.0,
                id = 2L,
                iconUrl = null,
                completedAtEpochMillis = null,
                affixes = emptyList(),
            ),
            DungeonRun(
                dungeonName = "City of Threads",
                shortName = "COT",
                keystoneLevel = 9,
                upgrades = 0,
                clearTime = 33.minutes,
                parTime = 30.minutes,
                score = 250.2,
                id = 3L,
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
