package org.creditbook.project.ui.transactions.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import org.creditbook.project.model.Customer
import org.creditbook.project.ui.common.OccurredAtPicker
import org.creditbook.project.ui.customers.CustomerHeaderContent

@Composable
fun AddPaymentContent(
    state: AddPaymentUiState,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOccurredAtChange: (LocalDateTime?) -> Unit,
    onPaymentMethodChange: (PaymentMethodOption) -> Unit,
    onPayFull: () -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    customer: Customer?
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (state.isOffline) {
            // En-tête customer
            CustomerHeaderContent(customer)
        } else if (!state.isLoadingCustomer) {
            // En-tête customer
            CustomerHeaderContent(state.customer)
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }


        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (state.showOfflineBalanceWarning) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        "Solde non disponible hors-ligne. Vous pouvez saisir un montant manuellement, mais vérifiez le solde une fois reconnecté.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = state.amountText,
                onValueChange = onAmountChange,
                label = { Text("Montant reçu (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.error != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.currentBalance != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onPayFull,
                        modifier = Modifier.weight(1f)
                    ) { Text("Tout payer") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Moyen de paiement", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.paymentMethod == PaymentMethodOption.CASH,
                    onClick = { onPaymentMethodChange(PaymentMethodOption.CASH) },
                    label = { Text("Espèces") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.paymentMethod == PaymentMethodOption.CARD,
                    onClick = { onPaymentMethodChange(PaymentMethodOption.CARD) },
                    label = { Text("Carte") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description (optionnel)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.willFullySettleDebt) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Text(
                        "Ardoise soldée après ce paiement",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OccurredAtPicker(
                occurredAt = state.occurredAt,
                onOccurredAtChange = onOccurredAtChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSubmit,
                enabled = state.isAmountValid && !state.isSubmitting,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirmer")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Retour")
            }
        }
    }
}