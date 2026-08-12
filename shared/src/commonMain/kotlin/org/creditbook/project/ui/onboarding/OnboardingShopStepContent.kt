package org.creditbook.project.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.Currency
import org.creditbook.project.ui.common.LimitedTextField


private val currencyOptions = listOf(
    Currency.EURO.code to "Euro (€)",
    Currency.USD.code to "Dollar US ($)",
    Currency.XOF.code to "Franc CFA (XOF)",
    Currency.XAF.code to "Franc CFA (XAF)",
    Currency.CD.code to "Franc Congolais (FC)"
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel =
        currencyOptions.firstOrNull { it.first == selectedCurrency }?.second ?: selectedCurrency

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Devise") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencyOptions.forEach { (code, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onCurrencyChange(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingShopStepContent(
    state: OnboardingUiState,
    modifier: Modifier = Modifier,
    onShopNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onShopPhoneChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        LinearProgressIndicator(progress = { 0.5f }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Text("Parlez-nous de votre commerce", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LimitedTextField(
            value = state.shopName, onValueChange = onShopNameChange,
            label = "Nom du commerce", singleLine = true,
            maxLength = 50,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.address, onValueChange = onAddressChange,
            label = "Adresse", singleLine = true,
            maxLength = 100,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LimitedTextField(
                value = state.postalCode, onValueChange = onPostalCodeChange,
                label = "Code postal", singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLength = 10,
                modifier = Modifier.weight(1f)
            )
            LimitedTextField(
                value = state.city, onValueChange = onCityChange,
                label = "Ville", singleLine = true,
                maxLength = 50,
                modifier = Modifier.weight(2f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.country, onValueChange = onCountryChange,
            label = { Text("Pays") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        LimitedTextField(
            value = state.shopPhone, onValueChange = onShopPhoneChange,
            label = "Téléphone du commerce", singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            maxLength = 20,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CurrencyDropdown(
            selectedCurrency = state.currency,
            onCurrencyChange = onCurrencyChange
        )

        state.error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            enabled = state.isShopStepValid,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Continuer")
        }
    }
}