package org.creditbook.project.ui.transactions

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import org.creditbook.project.model.CurrentCurrency
import org.creditbook.project.ui.common.LimitedTextField
import org.creditbook.project.ui.common.OccurredAtPicker
import org.creditbook.project.ui.common.dismissKeyboardOnTap

@Composable
fun CorrectEntryContent(
    state: CorrectEntryUiState,
    modifier: Modifier = Modifier,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onOccurredAtChange: (LocalDateTime?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .dismissKeyboardOnTap()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.amountText,
            onValueChange = onAmountChange,
            label = { Text("Montant (" + CurrentCurrency.value.symbol + ")") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = state.error != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = "Description (optionnel)",
            maxLength = 255,
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
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