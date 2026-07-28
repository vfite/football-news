package com.vfite.football.widget.data

import android.content.Context
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.data.jobs.SyncNewsJob
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RemoteSyncService(
    private val context: Context,
    private val widgetService: IWidgetService
) : SyncService {

    companion object {
        const val JOB_MODE = "job_mode"
        const val SYNC_NEWS_MODE = 1
        const val MATCHES_NEWS_MODE = 2
    }

    private var _innerState: MutableStateFlow<SyncState> = MutableStateFlow(SyncState.Synced)
    override val state: SyncState
        get() = _innerState.value

    override fun syncNews() {
        Timber.d("syncNews")
        updateState(SyncState.Syncing)
        widgetService.invalidateWidget()

        createWorker(SYNC_NEWS_MODE)
    }

    override fun syncMatches() {
        Timber.d("syncMatches")
        updateState(SyncState.Syncing)
        widgetService.invalidateWidget()

        createWorker(MATCHES_NEWS_MODE)
    }

    override fun updateState(synced: SyncState) {
        val curState = _innerState.value
        Timber.d("updateState from:$curState to $synced")
        GlobalScope.launch {
            _innerState.emit(synced)
        }
    }

    private fun createWorker(jobsMode: Int) {
        val workToRun = OneTimeWorkRequest.Builder(SyncNewsJob::class.java).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            }
            setInputData(
                Data.Builder()
                    .putInt(JOB_MODE, jobsMode)
                    .build())
        }.build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "NewsSyncService",
            ExistingWorkPolicy.REPLACE,
            workToRun
        )
    }
}