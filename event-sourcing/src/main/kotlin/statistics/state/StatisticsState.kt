package statistics.state

import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime
import statistics.model.UserStatistics

interface StatisticsState {
    suspend fun init(connection: SuspendingConnection)

    fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int)

    fun getUserStatistics(userId: Int): UserStatistics?
}
