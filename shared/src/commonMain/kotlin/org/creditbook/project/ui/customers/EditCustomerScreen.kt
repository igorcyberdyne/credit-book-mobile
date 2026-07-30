package org.creditbook.project.ui.customers


import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

data class EditCustomerScreen(
    val clientUuid: String,
    val currentFirstname: String,
    val currentLastname: String?,
    val currentPhone: String?,
    val currentNote: String?
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<EditCustomerViewModel>(key = clientUuid) {
            parametersOf(clientUuid, currentFirstname, currentLastname, currentPhone, currentNote)
        }
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.isSubmitted) {
            if (state.isSubmitted) navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Modifier le client") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { padding ->
            EditCustomerContent(
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