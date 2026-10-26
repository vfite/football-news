package com.vfite.football.api

import io.ktor.client.engine.HttpClientEngine

expect fun createHttpClientEngine(): HttpClientEngine
