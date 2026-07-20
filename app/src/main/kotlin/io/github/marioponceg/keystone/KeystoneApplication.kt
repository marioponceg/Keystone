package io.github.marioponceg.keystone

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.marioponceg.quill.QuillLevel
import io.github.marioponceg.quill.android.LogcatSink
import io.github.marioponceg.quill.quill

@HiltAndroidApp
class KeystoneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        quill {
            minLevel = if (BuildConfig.DEBUG) QuillLevel.Debug else QuillLevel.Info
            addSink(LogcatSink())
        }
    }
}
