package org.creditbook.project.ui.transactions

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
import kotlinx.datetime.LocalDateTime
import org.creditbook.project.model.Money
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class CorrectEntryScreen(
    val entryUuid: String,
    val currentAmount: Money,
    val currentDescription: String?,
    val currentOccurredAt: LocalDateTime?,
    val currentPaymentMethod: String?
) : Screen {
    private val screenKey = uuid4().toString()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinViewModel<CorrectEntryViewModel>(key = screenKey) {
            parametersOf(
                entryUuid,
                currentAmount,
                currentDescription,
                currentOccurredAt,
                currentPaymentMethod
            )
        }
        val state by viewModel.state.collectAsState()

        LaunchedEffect(state.isSubmitted) {
            if (state.isSubmitted) navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Corriger l'opération") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { padding ->
            CorrectEntryContent(
                state = state,
                modifier = androidx.compose.ui.Modifier.padding(padding),
                onAmountChange = viewModel::onAmountChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onOccurredAtChange = viewModel::onOccurredAtChange,
                onSubmit = viewModel::submit,
                onCancel = { navigator.pop() },
            )
        }
    }
}