package lamdx4.uis.ptithcm.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class RefreshCoordinator {
    private val _refreshFlows = mutableMapOf<String, MutableSharedFlow<Unit>>()

    fun getRefreshFlow(key: String): Flow<Unit> {
        return _refreshFlows.getOrPut(key) {
            MutableSharedFlow(extraBufferCapacity = 1)
        }
    }

    fun requestRefresh(key: String) {
        _refreshFlows[key]?.tryEmit(Unit)
    }
}
