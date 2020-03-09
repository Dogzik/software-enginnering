package gate.command

import com.github.jasync.sql.db.SuspendingConnection
import common.dao.CommonDao
import gate.model.GateEvent
import gate.model.GateEventType
import org.joda.time.LocalDateTime

class DatabaseGateCommandDao(private val connection: SuspendingConnection) : GateCommandDao, CommonDao() {
    override suspend fun processExit(userId: Int, time: LocalDateTime) = connection.inTransaction {
        val prevEvent = getLastGateEvent(it, userId).second
        if (prevEvent == null) {
            throw IllegalArgumentException("No previous event for user_id = $userId")
        } else {
            if (prevEvent.first.type != GateEventType.ENTER) {
                throw IllegalArgumentException("Previous event must be ENTER for user_id = $userId")
            }
            val newEventId = prevEvent.second + 1
            addGateEvent(it, userId, GateEvent(GateEventType.EXIT, time), newEventId)
            prevEvent.first.time to newEventId
        }
    }

    override suspend fun processEnter(userId: Int, time: LocalDateTime) = connection.inTransaction { transaction ->
        val (user, _) = getUserWithSubscription(transaction, userId)
        if (user == null) {
            throw IllegalArgumentException("No user with id = $userId")
        }
        if (user.subscriptionEnd?.let { !time.isBefore(it) } != false) {
            throw IllegalArgumentException("No suitable subscription for user_id = $userId")
        }
        val prevEvent = getLastGateEvent(transaction, userId).second
        if (prevEvent?.first?.type == GateEventType.ENTER) {
            throw IllegalArgumentException("Previous event was ENTER for user_id = $userId")
        }
        val newEventId = prevEvent?.second?.let { it + 1 } ?: 0
        addGateEvent(transaction, userId, GateEvent(GateEventType.ENTER, time), newEventId)
    }

    private suspend fun addGateEvent(
        transaction: SuspendingConnection,
        userId: Int,
        event: GateEvent,
        eventId: Int
    ): Unit {
        val query =
            """
                INSERT INTO gate_events (user_id, user_event_id, event_type, event_time)
                VALUES (?, ?, ?, ?)
            """.trimIndent()
        transaction.sendPreparedStatement(query, listOf(userId, eventId, event.type, event.time))
    }

    private suspend fun getLastGateEvent(
        transaction: SuspendingConnection,
        userId: Int
    ): Pair<String?, Pair<GateEvent, Int>?> {
        val query =
            """
                SELECT *
                FROM users
                         LEFT JOIN gate_events USING (user_id)
                WHERE user_id = ?
                ORDER BY user_event_id DESC
                LIMIT 1
            """.trimIndent()
        val result = transaction.sendPreparedStatement(query, listOf(userId)).rows
        return if (result.isEmpty()) {
            null to null
        } else {
            val name = result[0].getString("name")!!
            val id = result[0].getInt("user_event_id")
            if (id == null) {
                name to null
            } else {
                val type = GateEventType.valueOf(result[0].getString("event_type")!!)
                val time = result[0].getAs<LocalDateTime>("event_time")
                name to (GateEvent(type, time) to id)
            }
        }
    }
}
