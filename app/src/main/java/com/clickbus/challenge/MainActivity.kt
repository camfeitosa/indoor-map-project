package com.clickbus.challenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.clickbus.challenge.navigation.ClickBusNavGraph
import com.clickbus.challenge.ui.theme.ClickBusChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClickBusChallengeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ClickBusNavGraph()
                }
            }
        }
    }
}
