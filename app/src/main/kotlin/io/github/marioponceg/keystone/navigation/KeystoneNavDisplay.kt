package io.github.marioponceg.keystone.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.marioponceg.keystone.ui.character.CharacterDetailScreen
import io.github.marioponceg.keystone.ui.character.CharacterDetailViewModel
import io.github.marioponceg.keystone.ui.home.HomeScreen

@Composable
fun KeystoneNavDisplay(backStack: NavBackStack<NavKey>) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<HomeKey> {
                HomeScreen(onNavigateToCharacter = { id -> backStack.add(id.toKey()) })
            }
            entry<CharacterDetailKey> { key ->
                CharacterDetailScreen(
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = hiltViewModel<CharacterDetailViewModel, CharacterDetailViewModel.Factory>(
                        creationCallback = { factory -> factory.create(key) },
                    ),
                )
            }
        },
    )
}
