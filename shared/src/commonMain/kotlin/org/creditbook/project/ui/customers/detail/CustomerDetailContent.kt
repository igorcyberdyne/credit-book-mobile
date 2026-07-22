package org.creditbook.project.ui.customers.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.Customer
import org.creditbook.project.model.TransactionEntry
import org.creditbook.project.model.TransactionType
import org.creditbook.project.ui.customers.CustomerHeaderContent




@Composable
fun CustomerDetailContent(
    state: CustomerDetailUiState,
    onAddDebtClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onCancelEntry: (String) -> Unit
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
                    text = "Historique",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                    items(state.transactions) { entry ->
                        TransactionRow(entry = entry, onCancel = { onCancelEntry(entry.uuid) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(entry: TransactionEntry, onCancel: () -> Unit) {
    var showCancelConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showCancelConfirm = true }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.description ?: if (entry.type == TransactionType.DEBT) "Dette" else "Paiement",
                style = MaterialTheme.typography.bodyMedium
            )
            entry.occurredAt?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Text(
            text = (if (entry.type == TransactionType.DEBT) "+" else "-") + entry.amount.format(),
            color = if (entry.type == TransactionType.DEBT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("Annuler cette ligne ?") },
            text = { Text("Le solde du client sera recalculé après annulation.") },
            confirmButton = {
                TextButton(onClick = {
                    onCancel()
                    showCancelConfirm = false
                }) { Text("Confirmer", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) { Text("Retour") }
            }
        )
    }
}