package com.mishin.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mishin.core.ui.theme.MishinTheme

/**
 * Root composable that applies the Mishin theme and hosts navigation.
 */
@Composable
fun MishinApp() {
    MishinTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MishinNavHost()
        }
    }
}
