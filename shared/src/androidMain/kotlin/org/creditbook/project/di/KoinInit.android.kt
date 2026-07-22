package org.creditbook.project.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.creditbook.project.data.local.DatabaseDriverFactory
import org.creditbook.project.shared.db.AppDatabase
import org.creditbook.project.sync.ConnectivityObserver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun doInitKoinAndroid(context: Context) {
    initKoin {
        androidContext(context)
        modules(
            module {
                single<Settings> {
                    val masterKey = MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build()

                    val encryptedPrefs = EncryptedSharedPreferences.create(
                        context,
                        "auth_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )

                    SharedPreferencesSettings(encryptedPrefs)
                }
                single { ConnectivityObserver(get()) } // get() résout le Context Android via androidContext(...)
                single { DatabaseDriverFactory(get()) }
                single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
            }
        )
    }
}