package io.github.marioponceg.keystone.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.keystone.domain.repository.RecentSearchesRepository
import io.github.marioponceg.keystone.domain.repository.RegionPreferenceRepository
import io.github.marioponceg.keystone.storage.RecentSearchesDataStore
import io.github.marioponceg.keystone.storage.RegionPreferenceDataStore
import javax.inject.Singleton

private val Context.keystoneDataStore by preferencesDataStore(name = "keystone")

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.keystoneDataStore

    @Provides
    @Singleton
    fun provideRecentSearchesRepository(dataStore: DataStore<Preferences>): RecentSearchesRepository =
        RecentSearchesDataStore(dataStore)

    @Provides
    @Singleton
    fun provideRegionPreferenceRepository(dataStore: DataStore<Preferences>): RegionPreferenceRepository =
        RegionPreferenceDataStore(dataStore)
}
