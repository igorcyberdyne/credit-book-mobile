package org.creditbook.project.ui.auth


import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.creditbook.project.ui.customers.list.CustomerListScreen
import org.koin.compose.viewmodel.koinViewModel

object LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AuthViewModel>()

        LoginContent(
            viewModel = viewModel,
            onLoginSuccess = {
                navigator.replaceAll(CustomerListScreen)
            }
        )
    }
}

