package lamdx4.uis.ptithcm.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshCoordinator @Inject constructor() {
    private val _refreshEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val refreshEvent = _refreshEvent.asSharedFlow()

    fun sendRefresh(route: String) {
        _refreshEvent.tryEmit(route)
    }
}