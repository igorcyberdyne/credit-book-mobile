package org.creditbook.project.ui.transactions

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.creditbook.project.model.Customer
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

data class AddDebtScreen(val customerUuid: String, val customer: Customer?) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<AddDebtViewModel>(
            key = customerUuid
        ) { parametersOf(customerUuid) }

        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.isSubmitted) {
            if (state.isSubmitted) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nouvelle ardoise") },
                )
            }
        ) { padding ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.padding(
                    padding
                )
            ) {
                AddDebtContent(
                    state = state,
                    onAmountChange = viewModel::onAmountChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onSubmit = viewModel::submit,
                    onCancel = { navigator.pop() },
                    customer = customer
                )
            }
        }
    }
}