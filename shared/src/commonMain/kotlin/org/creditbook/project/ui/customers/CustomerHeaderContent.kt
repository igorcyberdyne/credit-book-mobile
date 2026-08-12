package org.creditbook.project.ui.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.Customer

data class CustomerHeader(
    val initials: String,
    val displayName: String,
    val phone: String?,
    val balanceFormatted: String,
    val hasDebt: Boolean,
) {

}

@Composable
fun CustomerHeaderContent(
    customer: Customer?
) {
    // En-tête customer
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (customer == null) {
            return;
        }

        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(customer.initials, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(customer.displayName, style = MaterialTheme.typography.titleLarge)
        customer.phone?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Doit actuellement", style = MaterialTheme.typography.bodySmall)
        Text(
            text = customer.balance.balance.format(),
            style = MaterialTheme.typography.headlineMedium,
            color = if (customer.balance.hasDebt) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

    }
}