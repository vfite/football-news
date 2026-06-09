package com.vfite.football.api

import io.ktor.client.engine.HttpClientEngine

/**
 * Platform-specific Ktor engine factory: OkHttp on Android, Darwin on iOS.
 */
expect fun createHttpClientEngine(): HttpClientEngine
