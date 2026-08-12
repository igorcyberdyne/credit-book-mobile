package org.creditbook.project.model

data class Shop(
    val uuid: String,
    val name: String,
    val address: String?,
    val postalCode: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val currency: String?
) {
    val displayName: String
        get() {
            return "Chez " + name.let {
                if (it.length > 15) it.take(15) + "..." else it
            }
        }

    val displayAddress: String
        get() {
            var fullAddress = ""
            val address = address ?: ""
            if (address.isNotEmpty()) {
                fullAddress = address
            }

            val postalCode: String =
                (city ?: "") + " " + (postalCode ?: "")
            if (postalCode.trim().isNotEmpty()) {
                fullAddress += (if (fullAddress.isNotEmpty()) ", " else "") + postalCode.trim()
            }

            return fullAddress.trim()
        }
}

data class Session(
    val user: User,
    val shop: Shop
)