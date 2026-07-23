package org.creditbook.project.ui.customers.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.TransactionEntry
import org.creditbook.project.model.TransactionType
import org.creditbook.project.ui.transactions.entryColorFor
import org.creditbook.project.ui.transactions.entryIconFor

@Composable
fun TransactionRowContent(
    entry: TransactionEntry,
    onCorrect: () -> Unit,
    onCancel: () -> Unit
) {
    var showActionSheet by remember { mutableStateOf(false) }
    var showCancelConfirm by remember { mutableStateOf(false) }

    val canAct = entry.canCorrect || entry.canReverse

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = canAct) { showActionSheet = true }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = entryIconFor(entry.icon),
                contentDescription = null,
                tint = entryColorFor(entry.color),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.description ?: if (entry.type == TransactionType.DEBT) "Dette" else "Paiement",
                    style = MaterialTheme.typography.bodyMedium,
                    color = entryColorFor(entry.color)
                )
                entry.occurredAt?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                entry.badge?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }

            Text(
                text = (if (entry.type == TransactionType.DEBT) "+" else "-") + entry.amount.format(),
                color = entryColorFor(entry.color),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showActionSheet) {
        AlertDialog(
            onDismissRequest = { showActionSheet = false },
            title = { Text("Que faire sur cette ligne ?") },
            text = { Text(entry.description ?: "Aucune description") },
            confirmButton = {
                if (entry.canCorrect) {
                    TextButton(onClick = {
                        showActionSheet = false
                        onCorrect()
                    }) { Text("Modifier") }
                }
            },
            dismissButton = {
                if (entry.canReverse) {
                    TextButton(onClick = {
                        showActionSheet = false
                        showCancelConfirm = true
                    }) { Text("Annuler la ligne", color = MaterialTheme.colorScheme.error) }
                }
            }
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