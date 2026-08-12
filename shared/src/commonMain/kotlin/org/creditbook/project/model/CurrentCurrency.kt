package org.creditbook.project.model

object CurrentCurrency {
    var value: Currency = Currency.UNDEFINED
        private set

    fun set(code: String) {
        value = Currency.fromCode(code)
    }

    fun reset() {
        value = Currency.UNDEFINED
    }
}