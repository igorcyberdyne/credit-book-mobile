package org.creditbook.project.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.creditbook.project.data.local.DatabaseDriverFactory
import org.creditbook.project.shared.db.AppDatabase
import org.creditbook.project.sync.ConnectivityObserver
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
fun doInitKoinIos() {
    initKoin {
        modules(
            module {
                single<Settings> { KeychainSettings(service = "org.creditbook.project.auth") }
                single { ConnectivityObserver() }
                single { DatabaseDriverFactory() }
                single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
            }
        )
    }
}