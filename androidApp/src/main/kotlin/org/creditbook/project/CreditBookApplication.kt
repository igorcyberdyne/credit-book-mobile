package org.creditbook.project

import android.app.Application
import org.creditbook.project.di.doInitKoinAndroid

class CreditBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        doInitKoinAndroid(this)
    }
}