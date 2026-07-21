package io.github.marioponceg.keystone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.keystone.data.realm.BundledRealmRepository
import io.github.marioponceg.keystone.domain.repository.RealmRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealmRepository(): RealmRepository = BundledRealmRepository()
}
