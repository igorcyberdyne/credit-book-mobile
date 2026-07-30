package org.creditbook.project.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.creditbook.project.ui.customers.list.CustomerListScreen

object HomeTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Clients",
            icon = rememberVectorPainter(Icons.Default.Home)
        )

    @Composable
    override fun Content() {
        Navigator(CustomerListScreen) { navigator ->
            TabRootState.isHomeAtRoot = navigator.lastItem == CustomerListScreen

            LaunchedEffect(Unit) {
                TabNavigatorResetEvents.resetRequests.collect { tab ->
                    if (tab == HomeTab && navigator.lastItem != CustomerListScreen) {
                        navigator.popUntilRoot()
                    }
                }
            }

            CurrentScreen()
        }
    }
}