package org.creditbook.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.creditbook.project.di.doInitKoinAndroid
import org.creditbook.project.ui.navigation.AppEntryPoint
import org.creditbook.project.ui.theme.AppTheme

class CreditBookApplication : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        doInitKoinAndroid(this)

        setContent {
            AppTheme {
                AppEntryPoint()
            }
        }
    }
}