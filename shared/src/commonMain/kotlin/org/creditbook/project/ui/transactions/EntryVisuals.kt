package org.creditbook.project.ui.transactions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun entryIconFor(icon: String?): ImageVector = when (icon) {
    "undo" -> Icons.AutoMirrored.Filled.Undo
    "plus" -> Icons.Default.Add
    "minus" -> Icons.Default.Remove
    "check" -> Icons.Default.Check
    "arrow_up" -> Icons.Default.ArrowUpward
    "arrow_down" -> Icons.Default.ArrowDownward
    "edit" -> Icons.Default.Edit
    else -> Icons.Default.Circle // fallback neutre si l'API renvoie une valeur inconnue
}

@Composable
fun entryColorFor(color: String?): Color = when (color) {
    "grey" -> MaterialTheme.colorScheme.onSurfaceVariant
    "red" -> MaterialTheme.colorScheme.error
    "green" -> MaterialTheme.colorScheme.primary
    else -> MaterialTheme.colorScheme.onSurface // fallback neutre
}