import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.joda.time.LocalDateTime

fun getUserSubscriptionConnection(
    userId: Int,
    name: String,
    eventId: Int?,
    until: LocalDateTime?
): SuspendingConnection {
    val connection = mockk<SuspendingConnection>()
    coEvery { connection.sendPreparedStatement(any(), listOf(userId)) }
        .answers {
            val rows = mockk<ResultSet>()
            every { rows.isEmpty() }.returns(false)
            val row = mockk<RowData>()
            every { row.getString("name") }.returns(name)
            every { row.getInt("user_event_id") }.returns(eventId)
            every { row.getAs<LocalDateTime?>("end_time") }.returns(until)
            every { rows[0] }.returns(row)
            QueryResult(0, "OK", rows)
        }
    return connection
}

fun getNoUserConnection(
    userId: Int
): SuspendingConnection {
    val connection = mockk<SuspendingConnection>()
    coEvery { connection.sendPreparedStatement(any(), listOf(userId)) }
        .answers {
            val rows = mockk<ResultSet>()
            every { rows.isEmpty() }.returns(true)
            QueryResult(0, "OK", rows)
        }
    return connection
}
