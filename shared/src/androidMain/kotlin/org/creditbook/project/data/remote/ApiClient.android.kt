package org.creditbook.project.data.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun httpClientEngine(): HttpClientEngineFactory<*> {
    return OkHttp
}