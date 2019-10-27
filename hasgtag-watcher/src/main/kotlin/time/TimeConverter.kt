package time

import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

fun toUnixTimestamp(date: Date) = TimeUnit.MILLISECONDS.toSeconds(date.time)

fun hoursBefore(date: Date, hours: Int): Date {
    require(hours >= 0) {
        throw IllegalArgumentException("hours can't be negative")
    }
    return Date(date.time - Duration.ofHours(hours.toLong()).toMillis())
}

fun getStartEndTimestamps(end: Date): Pair<Long, Long> {
    val start = hoursBefore(end, 1)
    return Pair(toUnixTimestamp(start), toUnixTimestamp(end))
}

fun getSearchTimestamps(baseTime: Date, prevHours: Int): List<Pair<Long, Long>> {
    require(prevHours >= 1) {
        throw IllegalArgumentException("Must be at least 1 hour")
    }
    return ((prevHours - 1) downTo 0).map { hoursBefore(baseTime, it) }.map { getStartEndTimestamps(it) }
}