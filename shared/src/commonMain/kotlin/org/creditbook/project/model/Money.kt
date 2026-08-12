package org.creditbook.project.model

import kotlin.math.roundToLong

enum class Currency(val code: String, val symbol: String) {
    EURO("EURO", "€"),
    USD("USD", "$"),
    CD("CD", "FC"),
    XOF("XOF", "CFA"),
    XAF("XAF", "FCFA"),
    UNDEFINED("UNDEFINED", "UND");

    companion object {
        fun fromCode(code: String): Currency =
            entries.firstOrNull { it.code == code } ?: UNDEFINED
    }
}

data class Money private constructor(
    val amountInCents: Long,
    val currency: Currency = CurrentCurrency.value
) {
    companion object {
        fun fromCents(amountInCents: Long, currency: Currency = CurrentCurrency.value): Money =
            Money(amountInCents, currency)

        // Équivalent de Money::fromDecimal côté PHP : parse une chaîne décimale ("10.00")
        // en centimes, sans passer par un Double brut pour le calcul final (arrondi explicite).
        fun fromDecimal(amount: String, currency: Currency = CurrentCurrency.value): Money {
            val cents = (amount.toDouble() * 100).roundToLong()
            return Money(cents, currency)
        }
    }

    fun cents(): Long = amountInCents

    // Équivalent de decimal() : "10.00"
    fun decimal(): String {
        val euros = amountInCents / 100
        val centsPart = kotlin.math.abs(amountInCents % 100).toString().padStart(2, '0')
        return "$euros.$centsPart"
    }

    // Équivalent de format() : "10,00 €"
    fun format(): String {
        val decimal = decimal()
        val parts = decimal.split(".")

        val integerPart = parts[0]
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        val decimalPart = parts[1]

        return "$integerPart,$decimalPart ${currency.symbol}"
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Impossible d'additionner deux devises différentes" }
        return Money(amountInCents + other.amountInCents, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Impossible de soustraire deux devises différentes" }
        return Money(amountInCents - other.amountInCents, currency)
    }

    fun isPositive(): Boolean = amountInCents > 0
    fun isZero(): Boolean = amountInCents == 0L
    fun isNegative(): Boolean = amountInCents < 0
}