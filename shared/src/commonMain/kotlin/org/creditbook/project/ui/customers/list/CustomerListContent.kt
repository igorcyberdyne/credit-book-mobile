package org.creditbook.project.ui.customers.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import org.creditbook.project.model.Customer


@Composable
@Preview(showSystemUi = true)
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
                items(state.customers) { customer ->
                    CustomerRow(customer = customer, onClick = { onCustomerClick(customer) })
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