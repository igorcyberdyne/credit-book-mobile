package org.creditbook.project.ui.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import org.creditbook.project.lib.formatDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun OccurredAtPicker(
    occurredAt: LocalDateTime?,
    onOccurredAtChange: (LocalDateTime?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingDate by remember { mutableStateOf<LocalDate?>(null) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Date de l'opération", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            Switch(
                checked = occurredAt != null,
                onCheckedChange = { checked ->
                    if (checked) {
                        showDatePicker = true
                    } else {
                        onOccurredAtChange(null)
                    }
                }
            )
        }

        if (occurredAt != null) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(formatLocalDateTime(occurredAt))
            }
        } else {
            Text("Maintenant", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = occurredAt
                ?.date
                ?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        pendingDate = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date
                        showDatePicker = false
                        showTimePicker = true
                    }
                }) { Text("Suivant") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timePickerState = rememberTimePickerState(
            initialHour = occurredAt?.hour ?: now.hour,
            initialMinute = occurredAt?.minute ?: now.minute
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Heure de l'opération") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val date = pendingDate ?: occurredAt?.date ?: now.date
                    onOccurredAtChange(
                        LocalDateTime(date, LocalTime(timePickerState.hour, timePickerState.minute))
                    )
                    showTimePicker = false
                }) { Text("Valider") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Annuler") }
            }
        )
    }
}


fun formatLocalDateTime(localDateTime: LocalDateTime): String {
    return try {
        val day = localDateTime.day.toString().padStart(2, '0')
        val month = localDateTime.month.number.toString().padStart(2, '0')
        val year = localDateTime.year
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')

        return "$day/$month/$year à $hour:$minute"
    } catch (_: Exception) {
        "" // fallback si le format ne parse pas, évite un crash d'affichage
    }
}