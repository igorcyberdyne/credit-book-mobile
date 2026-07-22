package org.creditbook.project.data.repository

import org.creditbook.project.data.remote.dto.CreateCustomerCommand
import org.creditbook.project.data.remote.dto.CustomerDto
import org.creditbook.project.data.remote.dto.CustomersPageDto
import org.creditbook.project.model.Customer
import org.creditbook.project.model.CustomersPage
import org.creditbook.project.model.toDomain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.remote.dto.ApiResponse

class CustomerRepository(
    private val httpClient: HttpClient,
) {
    suspend fun fetchCustomers(page: Int = 1, limit: Int = 20): CustomersPage {
        return httpClient.get("/api/customers") {
            parameter("page", page)
            parameter("limit", limit)
        }.body<ApiResponse<CustomersPageDto>>().data.toDomain()
    }

    suspend fun fetchCustomer(uuid: String): Customer {
        return httpClient.get("/api/customers/$uuid")
            .body<ApiResponse<CustomerDto>>().data
            .toDomain()
    }

    suspend fun createCustomer(
        command: CreateCustomerCommand
    ): Customer {
        return httpClient.post("/api/customers") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }.body<ApiResponse<CustomerDto>>().data.toDomain()
    }
}