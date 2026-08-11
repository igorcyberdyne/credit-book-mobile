package org.creditbook.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.creditbook.project.data.remote.dto.ApiResponse
import org.creditbook.project.data.remote.dto.CreateCustomerCommand
import org.creditbook.project.data.remote.dto.CustomerDto
import org.creditbook.project.data.remote.dto.CustomersPageDto
import org.creditbook.project.data.remote.dto.DashboardStatsDto
import org.creditbook.project.data.remote.dto.UpdateCustomerCommand
import org.creditbook.project.model.Customer
import org.creditbook.project.model.CustomersPage
import org.creditbook.project.model.DashboardStats
import org.creditbook.project.model.toDomain

class CustomerRepository(
    private val httpClient: HttpClient,
) {

    suspend fun fetchDashboardStats(): DashboardStats {
        return httpClient.get("/api/dashboard")
            .body<ApiResponse<DashboardStatsDto>>().data.toDomain()
    }

    suspend fun fetchCustomers(
        page: Int = 1,
        limit: Int = 20,
        search: String? = null
    ): CustomersPage {
        return httpClient.get("/api/customers") {
            parameter("page", page)
            parameter("limit", limit)
            search?.let { parameter("q", it) }
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

    suspend fun updateCustomer(
        uuid: String,
        command: UpdateCustomerCommand
    ): Customer {
        return httpClient.put("/api/customers/$uuid") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }.body<ApiResponse<CustomerDto>>().data.toDomain()
    }

}