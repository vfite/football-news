package com.vfite.football.di

import com.vfite.football.api.ApiClient
import com.vfite.football.api.ApiRepository
import com.vfite.football.api.KtorApiRepository
import com.vfite.football.api.createHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

/**
 * Binds the Ktor-based API client and [ApiRepository] implementation.
 * Must be imported alongside `sharedDomainModule()` before resolving
 * `RefreshDataUseCase` / `RefreshMatchesUseCase`.
 */
fun apiModule(baseUrl: String) = DI.Module("ApiModule") {

    bind<HttpClient>() with singleton {
        HttpClient(createHttpClientEngine()) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    bind<ApiClient>() with singleton {
        ApiClient(httpClient = instance(), baseUrl = baseUrl)
    }

    bind<ApiRepository>() with singleton {
        KtorApiRepository(apiClient = instance())
    }
}
