package org.creditbook.project.data.auth

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings

actual class SecureSettingsFactory {
    @OptIn(ExperimentalSettingsImplementation::class)
    actual fun create(): Settings = KeychainSettings(service = "org.creditbook.project.auth")
}