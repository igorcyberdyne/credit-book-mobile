package org.creditbook.project.ui.customers.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.TransactionEntry
import org.creditbook.project.ui.customers.CustomerHeaderContent


@Composable
fun CustomerDetailContent(
    state: CustomerDetailUiState,
    onAddDebtClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onCorrectEntry: (TransactionEntry) -> Unit,
    onCancelEntry: (String) -> Unit,
) {
    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erreur : ${state.error}")
            }
        }

        state.customer != null -> {
            val customer = state.customer

            Column(modifier = Modifier.fillMaxSize()) {
                // En-tête customer
                CustomerHeaderContent(customer)

                // Actions rapides
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onAddDebtClick, modifier = Modifier.weight(1f)) {
                        Text("Ajouter")
                    }
                    OutlinedButton(
                        onClick = onAddPaymentClick,
                        modifier = Modifier.weight(1f),
                        enabled = customer.balance.balance.isPositive()
                    ) {
                        Text("Encaisser")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Historique des opérations",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(state.transactions) { entry ->
                        TransactionRowContent(
                            entry = entry,
                            onCorrect = { onCorrectEntry(entry) },
                            onCancel = { onCancelEntry(entry.uuid) }
                        )
                    }
                }
            }
        }
    }
}