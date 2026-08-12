package org.creditbook.project.ui.common.error

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ErrorDialog() {
    val message = ErrorDialogState.message

    if (message != null) {
        AlertDialog(
            icon = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Dangerous,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Erreur",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

            },
            onDismissRequest = { ErrorDialogState.dismiss() },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { ErrorDialogState.dismiss() }) {
                    Text("OK")
                }
            }
        )
    }
}