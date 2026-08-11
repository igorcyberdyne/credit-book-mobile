package org.creditbook.project.di

import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.remote.createHttpClient
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.data.repository.CustomerRepository
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.ui.auth.AuthViewModel
import org.creditbook.project.ui.customers.EditCustomerViewModel
import org.creditbook.project.ui.customers.NewCustomerViewModel
import org.creditbook.project.ui.customers.detail.CustomerDetailViewModel
import org.creditbook.project.ui.customers.list.CustomerListViewModel
import org.creditbook.project.ui.onboarding.OnboardingViewModel
import org.creditbook.project.ui.transactions.CorrectEntryViewModel
import org.creditbook.project.ui.transactions.debt.AddDebtViewModel
import org.creditbook.project.ui.transactions.payment.AddPaymentViewModel
import org.koin.dsl.module

val appModule = module {
    single { TokenStorage(get()) }
    single {
        createHttpClient(
            baseUrl = "http://localhost:8080",
            tokenStorage = get(),
            onUnauthorized = {
                get<AuthRepository>().logout()
                AuthEvents.emitUnauthorized()
                // ici : émettre un événement global (ex. via un SharedFlow)
                // que l'UI observe pour naviguer vers l'écran de login
            }
        )
    }
    single { SessionDatabase(get()) }
    single { AuthRepository(get(), get(), get()) }
    single { CustomerRepository(get()) }
    single { TransactionRepository(get(), get(), get(), get()) }

    factory { AuthViewModel(get()) }
    factory { NewCustomerViewModel(get()) }
    factory { CustomerListViewModel(get(), get()) }
    factory { (clientUuid: String) -> CustomerDetailViewModel(clientUuid, get(), get()) }
    factory { (clientUuid: String) -> AddDebtViewModel(clientUuid, get()) }
    factory { (clientUuid: String) -> AddPaymentViewModel(clientUuid, get(), get(), get()) }
    factory { params ->
        CorrectEntryViewModel(
            entryUuid = params[0],
            initialAmount = params[1],
            initialDescription = params[2],
            initialOccurredAt = params[3],
            initialPaymentMethod = params[4],
            transactionRepository = get()
        )
    }
    factory { params ->
        EditCustomerViewModel(
            clientUuid = params[0],
            initialFirstname = params[1],
            initialLastname = params[2],
            initialPhone = params[3],
            initialNote = params[4],
            customerRepository = get()
        )
    }
    factory { OnboardingViewModel(get()) }
}