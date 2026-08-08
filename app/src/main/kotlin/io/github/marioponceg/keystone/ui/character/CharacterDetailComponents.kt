package io.github.marioponceg.keystone.ui.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.marioponceg.foundry.components.FoundryCard
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.components.FoundryTextStyle
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.domain.model.CharacterProfile
import io.github.marioponceg.keystone.domain.model.Role
import io.github.marioponceg.keystone.domain.model.RoleScore
import io.github.marioponceg.keystone.domain.model.SeasonScore
import io.github.marioponceg.keystone.ui.common.CharacterAvatar
import io.github.marioponceg.keystone.ui.common.WowClassColors
import io.github.marioponceg.keystone.ui.common.parseScoreColor
import java.util.Locale

@Composable
internal fun Header(profile: CharacterProfile, modifier: Modifier = Modifier) {
    val spacing = FoundryTheme.spacing
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CharacterAvatar(
            url = profile.avatarUrl,
            characterClass = profile.characterClass,
            characterName = profile.name,
        )
        Column {
            FoundryText(
                text = profile.name,
                style = FoundryTextStyle.Display,
                color = WowClassColors.forClass(profile.characterClass, FoundryTheme.colors.onBackground),
            )
            FoundryText(
                text = "${profile.spec} ${profile.characterClass} — ${profile.realm} (${profile.id.region.name})",
                style = FoundryTextStyle.Body,
                color = FoundryTheme.colors.onSurfaceMuted,
            )
        }
    }
}

@Composable
internal fun ScoreCard(score: SeasonScore) {
    val spacing = FoundryTheme.spacing
    val colors = FoundryTheme.colors
    FoundryCard(modifier = Modifier.fillMaxWidth()) {
        FoundryText(
            text = "%.1f".format(Locale.US, score.overall),
            style = FoundryTextStyle.Display,
            color = parseScoreColor(score.colorHex, colors.onSurface),
        )
        FoundryText(text = "M+ Score", style = FoundryTextStyle.Caption, color = colors.onSurfaceMuted)
        score.roles.forEach { role ->
            RoleRow(role = role, modifier = Modifier.padding(top = spacing.sm))
        }
    }
}

@Composable
private fun RoleRow(role: RoleScore, modifier: Modifier = Modifier) {
    val colors = FoundryTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        FoundryText(text = role.role.label(), style = FoundryTextStyle.Label)
        FoundryText(
            text = "%.1f".format(Locale.US, role.score),
            style = FoundryTextStyle.Label,
            color = parseScoreColor(role.colorHex, colors.onSurface),
        )
    }
}

private fun Role.label(): String = when (this) {
    Role.TANK -> "Tank"
    Role.HEALER -> "Healer"
    Role.DPS -> "DPS"
}
