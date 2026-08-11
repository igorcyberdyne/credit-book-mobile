package org.creditbook.project.ui.customers.detail


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import org.creditbook.project.lib.fromString
import org.creditbook.project.ui.customers.EditCustomerScreen
import org.creditbook.project.ui.customers.list.CustomerListContent
import org.creditbook.project.ui.transactions.CorrectEntryScreen
import org.creditbook.project.ui.transactions.debt.AddDebtScreen
import org.creditbook.project.ui.transactions.payment.AddPaymentScreen

data class CustomerDetailScreen(val customerUuid: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val viewModel = koinViewModel<CustomerDetailViewModel>(
            key = customerUuid
        ) { parametersOf(customerUuid) }

        val state by viewModel.state.collectAsState()


        val isCurrentScreen = navigator.lastItem == this
        DisposableEffect(isCurrentScreen) {
            if (isCurrentScreen) {
                viewModel.loadAll()
            }
            onDispose { }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(state.customer?.displayName ?: "") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    actions = {
                        state.customer?.let { customer ->
                            IconButton(onClick = {
                                navigator.push(
                                    EditCustomerScreen(
                                        clientUuid = customer.uuid,
                                        currentFirstname = customer.firstname,
                                        currentLastname = customer.lastname,
                                        currentPhone = customer.phone,
                                        currentNote = customer.note
                                    )
                                )
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Éditer")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                CustomerDetailContent(
                    state = state,
                    onAddDebtClick = {
                        navigator.push(
                            AddDebtScreen(
                                customerUuid,
                                state.customer
                            )
                        )
                    },
                    onAddPaymentClick = {
                        navigator.push(
                            AddPaymentScreen(
                                customerUuid,
                                state.customer
                            )
                        )
                    },
                    onCorrectEntry = { entry ->
                        navigator.push(
                            CorrectEntryScreen(
                                entryUuid = entry.uuid,
                                currentAmount = entry.amount,
                                currentDescription = entry.description,
                                currentOccurredAt = fromString(entry.occurredAt),
                                currentPaymentMethod = entry.paymentMethod
                            )
                        )
                    },
                    onCancelEntry = { entryUuid -> viewModel.cancelEntry(entryUuid) }
                )
            }
        }
    }
}