package org.creditbook.project.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import kotlinx.coroutines.launch

object MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            val tabNavigator = LocalTabNavigator.current

            Scaffold(
                bottomBar = {
                    // La bottom bar ne se dessine que si l'onglet actif
                    // est actuellement sur son écran racine.
                    if (isTabAtRoot(tabNavigator.current)) {
                        AppBottomBar()
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
    }

    fun isTabAtRoot(current: Tab): Boolean {
        print(current)
        return current is HomeTab || current is ProfileTab || current is SettingsTab
    }
}
@Composable
private fun AppBottomBar() {
    val tabNavigator = LocalTabNavigator.current
    val scope = rememberCoroutineScope()


    NavigationBar {
        listOf(HomeTab, ProfileTab, SettingsTab).forEach { tab ->
            NavigationBarItem(
                selected = tabNavigator.current == tab,
                onClick = {
                    if (tabNavigator.current == tab) {
                        scope.launch {
                            TabNavigatorResetEvents.requestReset(tab)
                        }
                    } else {
                        tabNavigator.current = tab
                    }
                },
                icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title) } },
                label = { Text(tab.options.title) }
            )
        }
    }
}