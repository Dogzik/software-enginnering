package common.dao

import com.github.jasync.sql.db.SuspendingConnection
import manager.model.User
import org.joda.time.LocalDateTime

abstract class CommonDao {
    protected suspend fun getUserWithSubscription(transaction: SuspendingConnection, userId: Int): Pair<User?, Int?> {
        val query =
            """
                SELECT *
                FROM users
                         LEFT JOIN subscription_events USING (user_id)
                WHERE user_id = ?
                ORDER BY user_event_id DESC
                LIMIT 1;
            """.trimIndent()
        val result = transaction.sendPreparedStatement(query, listOf(userId)).rows
        return if (result.isEmpty()) {
            Pair(null, null)
        } else {
            val name = result[0].getString("name")!!
            val subscriptionEnd = result[0].getAs<LocalDateTime?>("end_time")
            val eventId = result[0].getInt("user_event_id")
            Pair(User(userId, name, subscriptionEnd), eventId)
        }
    }
}
