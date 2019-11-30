package statistic

import clock.Clock
import java.time.Instant
import java.util.*
import java.time.temporal.ChronoUnit.HOURS
import kotlin.collections.HashMap

class RPMEventStatistic(private val clock: Clock) : EventStatistic {
    companion object {
        private val MINUTES_PER_HOUR = HOURS.duration.toMinutes().toDouble()
    }

    private val events: Queue<Pair<String, Instant>> = ArrayDeque()
    private val eventsCounters: MutableMap<String, Int> = HashMap()

    private fun needRemoveLastEvent(curTime: Instant): Boolean {
        val lastEventTime = events.peek().second
        return HOURS.between(lastEventTime, curTime) >= 1
    }

    private fun removeOldEvents(curTime: Instant) {
        while (!events.isEmpty() && needRemoveLastEvent(curTime)) {
            val (curName, _) = events.poll()
            val curCount = eventsCounters.getOrElse(curName) {
                throw AssertionError("Unknown event in queue")
            }
            when {
                curCount >= 2 -> {
                    eventsCounters[curName] = curCount - 1
                }
                curCount == 1 -> {
                    eventsCounters.remove(curName)
                }
                else -> {
                    throw AssertionError("Broken event counter")
                }
            }
        }
    }

    override fun incEvent(name: String) {
        val curTime = clock.now()
        removeOldEvents(curTime)
        events.add(Pair(name, curTime))
        eventsCounters.compute(name) { _, curCount -> curCount?.plus(1) ?: 1 }
    }

    override fun getEventStatisticByName(name: String): Double {
        val curTime = clock.now()
        removeOldEvents(curTime)
        return eventsCounters.getOrDefault(name, 0).toDouble() / MINUTES_PER_HOUR
    }

    override fun getAllEventsStatistic(): Map<String, Double> {
        val curTime = clock.now()
        removeOldEvents(curTime)
        return eventsCounters.mapValues { it.value.toDouble() / MINUTES_PER_HOUR }
    }

    override fun printStatistic() {
        println(getAllEventsStatistic())
    }
}