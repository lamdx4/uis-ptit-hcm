package lamdx4.uis.ptithcm.util

import java.time.Duration
import java.time.LocalDateTime

fun calculateInitialDelay(): Long {
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    return Duration.between(now, midnight).toMillis()
}
