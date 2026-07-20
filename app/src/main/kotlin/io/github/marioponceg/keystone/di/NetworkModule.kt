package io.github.marioponceg.keystone.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.marioponceg.conduit.ConduitClient
import io.github.marioponceg.conduit.conduit
import io.github.marioponceg.conduit.engine.okhttp.OkHttpEngine
import io.github.marioponceg.conduit.serialization.kotlinx.KotlinxJsonConverter
import io.github.marioponceg.keystone.BuildConfig
import io.github.marioponceg.keystone.data.remote.KeystoneJson
import io.github.marioponceg.keystone.data.remote.RaiderIoApi
import io.github.marioponceg.quill.conduit.BodyLevel
import io.github.marioponceg.quill.conduit.QuillInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideConduitClient(): ConduitClient = conduit {
        engine = OkHttpEngine()
        converter = KotlinxJsonConverter(KeystoneJson)
        baseUrl = "https://raider.io"
        interceptors += QuillInterceptor(
            level = if (BuildConfig.DEBUG) BodyLevel.Body else BodyLevel.Basic,
        )
    }

    @Provides
    @Singleton
    fun provideRaiderIoApi(client: ConduitClient): RaiderIoApi = RaiderIoApi(client)
}
