@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.marioponceg.keystone.ui.profile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.marioponceg.keystone.domain.model.CharacterId
import io.github.marioponceg.keystone.domain.model.Realm
import io.github.marioponceg.keystone.domain.model.Region

class ProfileStateProvider : PreviewParameterProvider<ProfileUiState> {
    override val values = sequenceOf(
        ProfileUiState.Empty,
        ProfileUiState.Content(
            listOf(
                CharacterId(Region.EU, Realm("Tarren Mill", "tarren-mill"), "Gingi"),
                CharacterId(Region.US, Realm("Illidan", "illidan"), "Dorki"),
            ),
        ),
    )
}
