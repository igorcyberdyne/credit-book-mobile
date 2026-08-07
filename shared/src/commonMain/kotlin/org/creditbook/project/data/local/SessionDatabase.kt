package org.creditbook.project.data.local

import org.creditbook.project.data.remote.dto.LoginResponse
import org.creditbook.project.model.Session
import org.creditbook.project.model.Shop
import org.creditbook.project.model.User
import org.creditbook.project.shared.db.AppDatabase

class SessionDatabase (
    private val database: AppDatabase
) {
    fun hasSession(): Boolean {
        val row = database.sessionQueries.hasSession().executeAsOneOrNull() ?: return false

        return row.toInt() > 0
    }


    fun saveSession(response: LoginResponse) {
        database.sessionQueries.insertOrReplaceSession(
            userUuid = response.user.uuid,
            email = response.user.email,
            firstName = response.user.firstName,
            lastName = response.user.lastName,
            roles = response.user.roles.joinToString(","),
            shopUuid = response.shop.uuid,
            shopName = response.shop.name,
            shopAddress = response.shop.address,
            shopPostalCode = response.shop.postalCode,
            shopCity = response.shop.city,
            shopCountry = response.shop.country,
            shopPhone = response.shop.phone,
            shopCurrency = response.shop.currency
        )
    }

    fun getCachedSession(): Session {
        val row = database.sessionQueries.selectSession().executeAsOne()

        return Session(
            user = User(
                uuid = row.userUuid,
                email = row.email,
                firstName = row.firstName,
                lastName = row.lastName,
                roles = row.roles.split(",")
            ),
            shop = Shop(
                uuid = row.shopUuid,
                name = row.shopName,
                address = row.shopAddress,
                postalCode = row.shopPostalCode,
                city = row.shopCity,
                country = row.shopCountry,
                phone = row.shopPhone,
                currency = row.shopCurrency
            )
        )
    }


    fun clearSession() {
        database.sessionQueries.clearSession()
    }
}