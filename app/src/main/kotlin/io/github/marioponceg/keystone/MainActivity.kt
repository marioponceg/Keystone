package io.github.marioponceg.keystone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.rememberNavBackStack
import dagger.hilt.android.AndroidEntryPoint
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.navigation.HomeKey
import io.github.marioponceg.keystone.navigation.KeystoneNavDisplay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FoundryTheme {
                KeystoneNavDisplay(backStack = rememberNavBackStack(HomeKey))
            }
        }
    }
}
