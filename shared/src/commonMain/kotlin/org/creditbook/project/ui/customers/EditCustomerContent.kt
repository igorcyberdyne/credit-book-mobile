package org.creditbook.project.ui.customers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun EditCustomerContent(
    state: EditCustomerUiState,
    modifier: Modifier = Modifier,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {

        OutlinedTextField(
            value = state.firstname,
            onValueChange = onFirstnameChange,
            label = { Text("Prénom") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            isError = state.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.lastname,
            onValueChange = onLastnameChange,
            label = { Text("Nom (optionnel)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = { Text("Téléphone") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
            ),
            isError = state.error?.contains("téléphone", ignoreCase = true) == true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.note,
            onValueChange = onNoteChange,
            label = { Text("Note (optionnel)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.fieldErrors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            state.fieldErrors.forEach {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            enabled = state.isValid && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Enregistrer")
            }
        }
    }
}