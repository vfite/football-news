package com.vfite.football.widget.data.jobs

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vfite.football.domain.FeedResult
import com.vfite.football.domain.onError
import com.vfite.football.domain.onSuccess
import com.vfite.football.widget.data.RemoteSyncService.Companion.JOB_MODE
import com.vfite.football.widget.data.RemoteSyncService.Companion.SYNC_NEWS_MODE
import com.vfite.football.widget.data.SyncService
import com.vfite.football.widget.data.SyncState
import com.vfite.football.widget.domain.RefreshDataUseCase
import com.vfite.football.widget.domain.RefreshMatchesUseCase
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance
import timber.log.Timber

class SyncNewsJob(
    private val context: Context,
    private val workerParams: WorkerParameters
) :
    CoroutineWorker(context, workerParams) {

    private val refreshDataUseCase: RefreshDataUseCase = DI.global.direct.instance()
    private val refreshMatchesUseCase: RefreshMatchesUseCase = DI.global.direct.instance()
    private val syncService: SyncService = DI.global.direct.instance()


    override suspend fun doWork(): Result {
        Timber.d("doWork")
        val jobsMode = inputData.getInt(JOB_MODE, SYNC_NEWS_MODE)
        return when (jobsMode) {
            SYNC_NEWS_MODE -> {
                syncNews()
            }
            else -> syncMatches()
        }
    }

    private suspend fun syncMatches(): Result {
        val result = refreshMatchesUseCase.execute()
        result.onSuccess {
            syncService.updateState(SyncState.Synced)
        }.onError {
            syncService.updateState(SyncState.Failed)
        }
        return if (result is FeedResult.Success) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private suspend fun syncNews(): Result {
        val result = refreshDataUseCase.execute()
        result.onSuccess {
            syncService.updateState(SyncState.Synced)
        }.onError {
            syncService.updateState(SyncState.Failed)
        }
        return if (result is FeedResult.Success) {
            Result.success()
        } else {
            Result.failure()
        }
    }

}