package gate

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import gate.command.DatabaseGateCommandDao
import getNoUserConnection
import getUserSubscriptionConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.joda.time.Period
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseGateCommandDaoTest {
    @Test(expected = IllegalArgumentException::class)
    fun testNoUserEnter() = runBlocking {
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> Unit>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Unit
                val transaction = getNoUserConnection(1)
                callback(transaction)
            }
        val dao = DatabaseGateCommandDao(connection)
        dao.processEnter(1, LocalDateTime.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNoSubscriptionEnter() = runBlocking {
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> Unit>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Unit
                val transaction = getUserSubscriptionConnection(1, "name", null, null)
                callback(transaction)
            }
        val dao = DatabaseGateCommandDao(connection)
        dao.processEnter(1, LocalDateTime.now())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAlreadyEnteredEnter() = runBlocking {
        val id = 1
        val until = LocalDateTime.parse("2030-01-01")
        val eventTime = LocalDateTime.parse("2020-01-01")
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> Unit>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Unit
                val transaction = mockk<SuspendingConnection>()
                coEvery { transaction.sendPreparedStatement(any(), listOf(id)) }
                    .answers {
                        val rows = mockk<ResultSet>()
                        every { rows.isEmpty() }.returns(false)
                        val row = mockk<RowData>()
                        every { row.getString("name") }.returns("name")
                        every { row.getInt("user_event_id") }.returns(11)
                        every { row.getAs<LocalDateTime?>("end_time") }.returns(until)
                        every { rows[0] }.returns(row)
                        QueryResult(0, "OK", rows)
                    }.andThen {
                        val rows = mockk<ResultSet>()
                        every { rows.isEmpty() }.returns(false)
                        val row = mockk<RowData>()
                        every { row.getString("name") }.returns("name")
                        every { row.getInt("user_event_id") }.returns(17)
                        every { row.getString("event_type") }.returns("ENTER")
                        every { row.getAs<LocalDateTime>("event_time") }.returns(eventTime.minus(Period.days(1)))
                        every { rows[0] }.returns(row)
                        QueryResult(0, "OK", rows)
                    }
                callback(transaction)
            }
        val dao = DatabaseGateCommandDao(connection)
        dao.processEnter(id, eventTime)
    }

    @Test
    fun testFirstEnter() = runBlocking {
        val until = LocalDateTime.parse("2030-01-01")
        val eventTime = LocalDateTime.parse("2020-01-01")
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> Unit>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Unit
                val transaction = mockk<SuspendingConnection>()
                coEvery { transaction.sendPreparedStatement(any(), any()) }
                    .answers {
                        val rows = mockk<ResultSet>()
                        every { rows.isEmpty() }.returns(false)
                        val row = mockk<RowData>()
                        every { row.getString("name") }.returns("name")
                        every { row.getInt("user_event_id") }.returns(11)
                        every { row.getAs<LocalDateTime?>("end_time") }.returns(until)
                        every { rows[0] }.returns(row)
                        QueryResult(0, "OK", rows)
                    }.andThen {
                        val rows = mockk<ResultSet>()
                        every { rows.isEmpty() }.returns(false)
                        val row = mockk<RowData>()
                        every { row.getString("name") }.returns("name")
                        every { row.getInt("user_event_id") }.returns(17)
                        every { row.getString("event_type") }.returns("EXIT")
                        every { row.getAs<LocalDateTime>("event_time") }.returns(eventTime.minus(Period.days(1)))
                        every { rows[0] }.returns(row)
                        QueryResult(0, "OK", rows)
                    }.andThen {
                        QueryResult(1, "OK")
                    }
                callback(transaction)
            }
        val dao = DatabaseGateCommandDao(connection)
        val result = dao.processEnter(1, eventTime)
        assertEquals(Unit, result)
    }
}
