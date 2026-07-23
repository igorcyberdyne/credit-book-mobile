package org.creditbook.project.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CorrectEntryContent(
    state: CorrectEntryUiState,
    modifier: Modifier = Modifier,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.amountText,
            onValueChange = onAmountChange,
            label = { Text("Montant (€)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = state.error != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text("Description (optionnel)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

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
                Text("Valider la correction")
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