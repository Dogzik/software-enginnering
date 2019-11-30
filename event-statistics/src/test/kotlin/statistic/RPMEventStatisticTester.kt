package statistic

import clock.ControlledClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import java.time.Duration
import java.time.Instant
import kotlin.math.abs

class RPMEventStatisticTester {
    private val EPS = 1e-6

    private fun equalStats(a: Map<String, Double>, b: Map<String, Double>): Boolean {
        if (a.keys != b.keys) {
            return false
        }
        for ((key, value) in a) {
            val otherValue = b[key]!!
            if (abs(value - otherValue) > EPS) {
                return false
            }
        }
        return true
    }

    private var clock = ControlledClock(Instant.now())
    private var stats = RPMEventStatistic(clock)

    @Before
    fun prepareData() {
        val start = Instant.now()
        clock = ControlledClock(start)
        stats = RPMEventStatistic(clock)
    }

    @Test
    fun simpleTest() {
        stats.incEvent("a")
        stats.incEvent("b")
        stats.incEvent("a")

        val expected = mapOf(Pair("a", 2.0 / 60.0), Pair("b", 1.0 / 60.0))
        assertTrue(equalStats(stats.getAllEventsStatistic(), expected))
    }

    @Test
    fun absentEventTest() {
        stats.incEvent("c")
        assertEquals(0.0, stats.getEventStatisticByName("a"), EPS)
    }

    @Test
    fun allEventsGoneTest() {
        stats.incEvent("a")
        stats.incEvent("b")
        clock.plus(Duration.ofHours(1))
        clock.plus(Duration.ofMinutes(2))
        assertEquals(emptyMap<String, Double>(), stats.getAllEventsStatistic())
    }

    @Test
    fun forgetEventTest() {
        stats.incEvent("a")
        clock.plus(Duration.ofMinutes(30))
        stats.incEvent("b")
        clock.plus(Duration.ofMinutes(40))
        val expected = mapOf(Pair("b", 1.0 / 60))
        assertTrue(equalStats(stats.getAllEventsStatistic(), expected))
    }

    @Test
    fun forgerEventPartially() {
        stats.incEvent("a")
        stats.incEvent("a")
        clock.plus(Duration.ofMinutes(30))
        stats.incEvent("a")
        stats.incEvent("a")
        stats.incEvent("a")
        stats.incEvent("b")
        stats.incEvent("b")
        clock.plus(Duration.ofMinutes(35))
        assertEquals(3.0 / 60, stats.getEventStatisticByName("a"), EPS)
        assertEquals(2.0 / 60, stats.getEventStatisticByName("b"), EPS)
    }
}