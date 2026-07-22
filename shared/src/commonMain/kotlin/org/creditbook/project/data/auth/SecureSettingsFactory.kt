package org.creditbook.project.data.auth

import com.russhwolf.settings.Settings

expect class SecureSettingsFactory {
    fun create(): Settings
}