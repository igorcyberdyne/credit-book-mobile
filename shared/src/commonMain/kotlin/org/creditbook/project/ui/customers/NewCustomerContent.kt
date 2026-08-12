package org.creditbook.project.ui.customers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import org.creditbook.project.ui.common.LimitedTextField
import org.creditbook.project.ui.common.dismissKeyboardOnTap

@Composable
fun NewCustomerContent(
    state: NewCustomerUiState,
    modifier: Modifier = Modifier,
    onFirstnameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .dismissKeyboardOnTap()
            .padding(16.dp)
    ) {

        LimitedTextField(
            value = state.firstname,
            onValueChange = onFirstnameChange,
            label = "Prénom",
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            maxLength = 100,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.lastname,
            onValueChange = onLastnameChange,
            label = "Nom (optionnel)",
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            maxLength = 100,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = "Téléphone (optionnel)",
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
            ),
            maxLength = 20,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.note,
            onValueChange = onNoteChange,
            label = "Note (optionnel)",
            maxLength = 255,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.fieldErrors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            state.fieldErrors.forEach {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
                Text("Créer le client")
            }
        }
    }
}