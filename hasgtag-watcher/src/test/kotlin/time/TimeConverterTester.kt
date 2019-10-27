package time

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TimeConverterTester {
    private fun getRandomTs(from: Long = 0, to: Long = Int.MAX_VALUE.toLong()) = Random.Default.nextLong(from, to)

    private fun tsToDate(ts: Long) = Date(TimeUnit.SECONDS.toMillis(ts))

    private fun getRandomDate() = tsToDate(getRandomTs())

    @Test
    fun testToUnixTimestamp() {
        for (i in (1..1000)) {
            val ts = getRandomTs()
            val date = tsToDate(ts)
            assertEquals(toUnixTimestamp(date), ts)
        }
    }

    @Test
    fun testZeroHoursBefore() {
        val date = getRandomDate()
        assertEquals(date, hoursBefore(date, 0))
    }

    @Test
    fun testHoursBefore() {
        for (i in (1..100)) {
            val hours = Random.Default.nextInt(1, 1000)
            val maxTs = Int.MAX_VALUE.toLong() - TimeUnit.HOURS.toSeconds(hours.toLong())
            val prevTs = getRandomTs(0, maxTs)
            val curTs = prevTs + TimeUnit.HOURS.toSeconds(hours.toLong())
            val curDate = tsToDate(curTs)
            val prevDate = tsToDate(prevTs)
            assertEquals(hoursBefore(curDate, hours), prevDate)
        }
    }
}