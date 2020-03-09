package statistics

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.junit.Assert.assertEquals
import org.junit.Test
import statistics.model.UserStatistics
import statistics.state.MemoryStatisticsState

class MemoryStatisticsStateTest {
    @Test
    fun testEmptyStatistics() = runBlocking {
        val connection = getEmptyInitConnection()
        val state = MemoryStatisticsState()
        state.init(connection)
        assertEquals(null, state.getUserStatistics(2))
    }

    @Test
    fun testEmptyStatisticsWithAddition() = runBlocking {
        val connection = getEmptyInitConnection()
        val state = MemoryStatisticsState()
        state.init(connection)
        val startTime = LocalDateTime.parse("2020-01-01")
        val period = Period.days(1)
        val endTime = startTime.plus(period)
        state.addVisit(1, startTime, endTime, 1)
        val expected = UserStatistics(1, period.normalizedStandard())
        val real = state.getUserStatistics(1)
        val normReal = real!!.copy(totalTimeSpent = real.totalTimeSpent.normalizedStandard())
        assertEquals(expected, normReal)
    }

    @Test
    fun testNotEmptyInit() = runBlocking {
        val connection = mockk<SuspendingConnection>()
        coEvery {
            connection.sendPreparedStatement(any())
        }.answers {
            val rows = mockk<ResultSet>()
            val iterator = mockk<Iterator<RowData>>()
            every { iterator.hasNext() }.returns(true).andThen(false)
            every { iterator.next() }.answers {
                val row = mockk<RowData>()
                every { row.getInt("user_id") }.returns(1)
                every { row.getLong("total_visits") }.returns(2)
                every { row.getAs<Period>("total_time") }.returns(Period.days(2))
                every { row.getInt("last_event_id") }.returns(1)
                row
            }
            every { rows.iterator() }.returns(iterator)
            QueryResult(0, "OK", rows)
        }
        val state = MemoryStatisticsState()
        state.init(connection)
        val startTime = LocalDateTime.parse("2020-01-01")
        val period = Period.days(1)
        val endTime = startTime.plus(period)
        state.addVisit(1, startTime, endTime, 2)
        state.addVisit(2, startTime, endTime, 1)
        val expected1 = UserStatistics(3, Period.days(3).normalizedStandard())
        val expected2 = UserStatistics(1, Period.days(1).normalizedStandard())
        val real1 = state.getUserStatistics(1)!!
        val real2 = state.getUserStatistics(2)!!
        val norm1 = real1.copy(totalTimeSpent = real1.totalTimeSpent.normalizedStandard())
        val norm2 = real2.copy(totalTimeSpent = real2.totalTimeSpent.normalizedStandard())
        assertEquals(expected1, norm1)
        assertEquals(expected2, norm2)
    }

    private fun getEmptyInitConnection(): SuspendingConnection {
        val connection = mockk<SuspendingConnection>()
        coEvery {
            connection.sendPreparedStatement(any())
        }.answers {
            val rows = mockk<ResultSet>()
            val iterator = mockk<Iterator<RowData>>()
            every { iterator.hasNext() }.returns(false)
            every { rows.iterator() }.returns(iterator)
            QueryResult(0, "OK", rows)
        }
        return connection
    }
}
