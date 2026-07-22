package org.creditbook.project.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModule) // le module commun vu précédemment (AuthRepository, ClientRepository...)
    }
}
