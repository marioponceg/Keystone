package io.github.marioponceg.keystone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.marioponceg.foundry.tokens.FoundryTheme
import io.github.marioponceg.keystone.navigation.KeystoneNavDisplay
import io.github.marioponceg.keystone.ui.common.StatusBarProtection

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FoundryTheme {
                // The protection is drawn last so it sits above every screen: content scrolling
                // behind the status bar is a property of the whole app, not of one pane, and
                // putting it here means no future screen can forget it.
                Box(modifier = Modifier.fillMaxSize()) {
                    KeystoneNavDisplay()
                    StatusBarProtection()
                }
            }
        }
    }
}
