package io.github.marioponceg.keystone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.marioponceg.foundry.components.FoundryText
import io.github.marioponceg.foundry.tokens.FoundryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoundryTheme {
                FoundryText(text = "Keystone")
            }
        }
    }
}
