package io.github.marioponceg.keystone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.CharacterRepository
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository
import io.github.marioponceg.keystone.domain.usecase.GetCharacterProfile
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes
import io.github.marioponceg.keystone.domain.usecase.ObserveRecentSearches
import io.github.marioponceg.keystone.domain.usecase.ObserveSelectedRegion
import io.github.marioponceg.keystone.domain.usecase.RemoveRecentSearch
import io.github.marioponceg.keystone.domain.usecase.SaveRecentSearch
import io.github.marioponceg.keystone.domain.usecase.SaveSelectedRegion

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetCharacterProfile(repository: CharacterRepository): GetCharacterProfile =
        GetCharacterProfile(repository)

    @Provides
    fun provideGetWeeklyAffixes(repository: AffixesRepository): GetWeeklyAffixes =
        GetWeeklyAffixes(repository)

    @Provides
    fun provideObserveRecentSearches(repository: RecentSearchesRepository): ObserveRecentSearches =
        ObserveRecentSearches(repository)

    @Provides
    fun provideSaveRecentSearch(repository: RecentSearchesRepository): SaveRecentSearch =
        SaveRecentSearch(repository)

    @Provides
    fun provideRemoveRecentSearch(repository: RecentSearchesRepository): RemoveRecentSearch =
        RemoveRecentSearch(repository)

    @Provides
    fun provideObserveSelectedRegion(repository: RegionPreferenceRepository): ObserveSelectedRegion =
        ObserveSelectedRegion(repository)

    @Provides
    fun provideSaveSelectedRegion(repository: RegionPreferenceRepository): SaveSelectedRegion =
        SaveSelectedRegion(repository)
}
