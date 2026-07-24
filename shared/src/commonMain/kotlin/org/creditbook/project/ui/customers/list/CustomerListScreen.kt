package org.creditbook.project.ui.customers.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.creditbook.project.ui.customers.NewCustomerScreen
import org.creditbook.project.ui.customers.detail.CustomerDetailScreen
import org.koin.compose.viewmodel.koinViewModel

object CustomerListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<CustomerListViewModel>()
        val state by viewModel.state.collectAsState()


        val isCurrentScreen = navigator.lastItem == this

        DisposableEffect(isCurrentScreen) {
            if (isCurrentScreen) {
                viewModel.refresh()
            }
            onDispose { }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.stats?.shopName ?: "Dashboard") },
                    actions = {
                        IconButton(onClick = { navigator.push(NewCustomerScreen()) }) {
                            Icon(Icons.Default.Add, contentDescription = "Nouveau client")
                        }
                    }
                )
            }
        ) { padding ->
            androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.padding(padding)) {
                CustomerListContent(
                    state = state,
                    onCustomerClick = { customer -> navigator.push(CustomerDetailScreen(customer.uuid)) },
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onLoadMore = { viewModel.loadNextPage() }
                )
            }
        }
    }
}


