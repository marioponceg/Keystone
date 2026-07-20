package io.github.marioponceg.keystone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.CharacterRepository
import io.github.marioponceg.keystone.domain.usecase.GetCharacterProfile
import io.github.marioponceg.keystone.domain.usecase.GetWeeklyAffixes

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetCharacterProfile(repository: CharacterRepository): GetCharacterProfile =
        GetCharacterProfile(repository)

    @Provides
    fun provideGetWeeklyAffixes(repository: AffixesRepository): GetWeeklyAffixes =
        GetWeeklyAffixes(repository)
}
