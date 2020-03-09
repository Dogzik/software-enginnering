package statistics.state

import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime
import org.joda.time.Period
import statistics.model.UserStatistics
import java.util.concurrent.ConcurrentHashMap

class MemoryStatisticsState : StatisticsState {
    /**
     * Can be called only once and before addVisit
     */
    override suspend fun init(connection: SuspendingConnection) {
        val statsQuery =
            """
                WITH ranked_events AS (
                    SELECT user_id,
                           event_type,
                           event_time,
                           user_event_id,
                           rank() OVER (PARTITION BY (user_id, event_type) ORDER BY user_event_id) AS num
                    FROM gate_events
                ),
                     exits AS (
                         SELECT user_id,
                                num,
                                event_time    AS exit_time,
                                user_event_id AS exit_id
                         FROM ranked_events
                         WHERE event_type = 'EXIT'
                     ),
                     enters AS (
                         SELECT user_id,
                                num,
                                event_time AS enter_time
                         FROM ranked_events
                         WHERE event_type = 'ENTER'
                     )
                SELECT user_id,
                       count(1)                    AS total_visits,
                       sum(exit_time - enter_time) AS total_time,
                       max(exit_id)                AS last_event_id
                FROM exits
                         JOIN enters USING (user_id, num)
                GROUP BY user_id
            """.trimIndent()
        val rawStats = connection.sendPreparedStatement(statsQuery).rows
        for (row in rawStats) {
            val userId = row.getInt("user_id")!!
            val totalVisits = row.getLong("total_visits")!!.toInt()
            val totalTime = row.getAs<Period>("total_time")
            val lastEventId = row.getInt("last_event_id")!!.toInt()
            state[userId] = UserStatistics(totalVisits, totalTime) to lastEventId
        }
    }

    override fun addVisit(userId: Int, startTime: LocalDateTime, endTime: LocalDateTime, eventId: Int) {
        val visitPeriod = Period.fieldDifference(startTime, endTime)
        state.compute(userId) { _, data ->
            if (data == null) {
                UserStatistics(1, visitPeriod) to eventId
            } else {
                val (stats, lastEventId) = data
                if (eventId <= lastEventId) {
                    data
                } else {
                    val newStats = UserStatistics(stats.totalVisits + 1, stats.totalTimeSpent + visitPeriod)
                    newStats to eventId
                }
            }
        }
    }

    override fun getUserStatistics(userId: Int): UserStatistics? = state[userId]?.first

    private val state = ConcurrentHashMap<Int, Pair<UserStatistics, Int>>()
}
