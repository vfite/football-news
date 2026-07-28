package com.vfite.football.widget.data

interface SyncService {

    val state: SyncState

    fun syncNews()
    fun syncMatches()
    fun updateState(synced: SyncState)
}

sealed class SyncState {
    object Failed: SyncState()
    object Syncing: SyncState()
    object Synced: SyncState()
    object None: SyncState()
}