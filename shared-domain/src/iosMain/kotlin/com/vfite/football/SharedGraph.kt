package com.vfite.football

import com.vfite.football.data.MatchesRepository
import com.vfite.football.data.NewsRepository
import com.vfite.football.data.db.createDatabaseBuilder
import com.vfite.football.di.apiModule
import com.vfite.football.di.databaseModule
import com.vfite.football.di.sharedDomainModule
import com.vfite.football.domain.RefreshDataUseCase
import org.kodein.di.DI
import org.kodein.di.instance

/**
 * Swift's single entry point into `:shared-domain`: builds the shared DI graph so Swift never touches Kodein.
 * Create one per process (app or widget extension) and keep it for the process lifetime.
 *
 * Reads (`getNews()`, `getTodayGames()`) and `RefreshDataUseCase.execute()` never throw; Swift imports them
 * as `async throws` only because of the Kotlin/Native export. They must be called on the main actor —
 * Kotlin/Native's default thread restriction — and do their work on `Dispatchers.IO`.
 *
 * The constructor throws when [appGroupId] has no container (missing entitlement).
 */
class SharedGraph @Throws(IllegalStateException::class) constructor(appGroupId: String, baseUrl: String) {

    private val di = DI {
        import(apiModule(baseUrl = baseUrl))
        import(databaseModule(createDatabaseBuilder(appGroupId)))
        import(sharedDomainModule())
    }

    val newsRepository: NewsRepository by di.instance()

    val matchesRepository: MatchesRepository by di.instance()

    /** Refreshes News and Today's Matches; the result reports News only. iOS has no `SyncService`. */
    val refreshDataUseCase: RefreshDataUseCase by di.instance()
}
