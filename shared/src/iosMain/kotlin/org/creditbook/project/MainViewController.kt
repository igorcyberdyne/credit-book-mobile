package org.creditbook.project

import androidx.compose.ui.window.ComposeUIViewController
import org.creditbook.project.ui.navigation.AppEntryPoint
import org.creditbook.project.ui.theme.AppTheme

fun MainViewController() = ComposeUIViewController {
    AppTheme {
        AppEntryPoint()
    }
}