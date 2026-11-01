package com.abeerapps.kmoviecomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abeerapps.kmoviecomposeapp.ui.navigation.AppNavHost
import com.abeerapps.kmoviecomposeapp.ui.theme.KMovieComposeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KMovieComposeAppTheme {
                AppNavHost()
            }
        }
    }
}
