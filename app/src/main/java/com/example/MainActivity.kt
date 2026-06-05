package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            val isDarkModeState by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val darkTheme = isDarkModeState ?: systemDark

            MyApplicationTheme(darkTheme = darkTheme) {
                MainNavigation(viewModel)
            }
        }
    }
}
