package org.creditbook.project.ui.customers.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.creditbook.project.model.Customer


@Composable
fun CustomerListContent(
    state: CustomerListUiState,
    onCustomerClick: (Customer) -> Unit,
    onLoadMore: () -> Unit
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

        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                /*items(state.customers) { customer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        CustomerRow(customer = customer, onClick = { onCustomerClick(customer) })
                    }
                }*/
                itemsIndexed(state.customers) { index, customer ->
                    CustomerRow(customer = customer, onClick = { onCustomerClick(customer) })
                    if (index < state.customers.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 62.dp), // aligné après l'avatar, pas toute la largeur
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                if (state.hasNextPage) {
                    item {
                        LaunchedEffect(Unit) { onLoadMore() }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}