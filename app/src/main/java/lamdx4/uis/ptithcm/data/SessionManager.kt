package lamdx4.uis.ptithcm.data

import lamdx4.uis.ptithcm.data.repository.Cacheable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val cacheables: Set<@JvmSuppressWildcards Cacheable>
) {
    fun logout() {
        cacheables.forEach { it.clearCache() }
    }
}