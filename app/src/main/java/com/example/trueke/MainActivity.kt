package com.example.trueke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.trueke.navigation.AppNavigation
import com.example.trueke.ui.theme.TruekeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            TruekeTheme {

                AppNavigation()

            }
        }
    }
}