package org.creditbook.project.lib

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun formatDate(date: String?): String {
    if (date == null) return ""

    return try {
        val instant = Instant.parse(date)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = localDateTime.day.toString().padStart(2, '0')
        val month = localDateTime.month.number.toString().padStart(2, '0')
        val year = localDateTime.year
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val now = "${currentDate.day}/${currentDate.month.number.toString().padStart(2, '0')}/${currentDate.year}"

        var localDate = "$day/$month/$year"
        localDate = if (now == localDate) "aujourd'hui" else localDate

        "$localDate à $hour:$minute"
    } catch (_: Exception) {
        date // fallback si le format ne parse pas, évite un crash d'affichage
    }
}

@OptIn(ExperimentalTime::class)
fun fromString(date: String?): LocalDateTime? {
    if (date == null) return null

    return try {
        Instant.parse(date).toLocalDateTime(TimeZone.currentSystemDefault())
    } catch (_: Exception) {
        null // fallback si le format ne parse pas, évite un crash d'affichage
    }
}