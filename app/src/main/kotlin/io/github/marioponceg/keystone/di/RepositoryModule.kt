package io.github.marioponceg.keystone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.keystone.data.remote.RaiderIoApi
import io.github.marioponceg.keystone.data.repository.AffixesRepositoryImpl
import io.github.marioponceg.keystone.data.repository.CharacterRepositoryImpl
import io.github.marioponceg.keystone.domain.repository.AffixesRepository
import io.github.marioponceg.keystone.domain.repository.CharacterRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCharacterRepository(api: RaiderIoApi): CharacterRepository = CharacterRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideAffixesRepository(api: RaiderIoApi): AffixesRepository = AffixesRepositoryImpl(api)
}
