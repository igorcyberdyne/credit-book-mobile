package org.creditbook.project.ui.customers

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.uuid4
import org.creditbook.project.ui.transactions.debt.AddDebtScreen
import org.koin.compose.viewmodel.koinViewModel

class NewCustomerScreen : Screen {
    private val screenKey = uuid4().toString()

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<NewCustomerViewModel>(key = screenKey)
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.customer) {
            state.customer?.let { customer ->
                // Remplace l'écran de création par l'ajout de dette :
                // un retour depuis AddDebtScreen ramène directement à la liste,
                // pas à un formulaire de création vide.
                navigator.replace(AddDebtScreen(customer.uuid, customer))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nouvelle ardoise") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { padding ->
            NewCustomerContent(
                state = state,
                modifier = androidx.compose.ui.Modifier.padding(padding),
                onFirstnameChange = viewModel::onFirstnameChange,
                onLastnameChange = viewModel::onLastnameChange,
                onPhoneChange = viewModel::onPhoneChange,
                onNoteChange = viewModel::onNoteChange,
                onSubmit = viewModel::submit
            )
        }

    }
}