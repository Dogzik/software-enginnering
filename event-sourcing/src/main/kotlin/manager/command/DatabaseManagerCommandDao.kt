package manager.command

import com.github.jasync.sql.db.SuspendingConnection
import common.dao.CommonDao
import org.joda.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference

class DatabaseManagerCommandDao(
    private val connection: SuspendingConnection,
    private val poolSize: Int = 15
) : ManagerCommandDao, CommonDao() {
    override suspend fun createUser(name: String): Int = connection.inTransaction {
        val newId = getNewId(it)
        val insertUserCommand =
            """
                INSERT INTO users (user_id, name)
                VALUES (?, ?)
            """.trimIndent()
        it.sendPreparedStatement(insertUserCommand, listOf(newId, name))
        newId
    }

    override suspend fun renewSubscription(userId: Int, until: LocalDateTime) = connection.inTransaction { transaction ->
        val curTime = LocalDateTime.now()
        if (!curTime.isBefore(until)) {
            throw IllegalArgumentException("Cannot review past($until) at now($curTime)")
        }
        val (user, eventId) = getUserWithSubscription(transaction, userId)
        if (user == null) {
            throw IllegalArgumentException("No user with user_id = $userId")
        }
        if (user.subscriptionEnd?.let { !it.isBefore(until) } == true) {
            throw IllegalArgumentException("Already subscribed for longer period")
        }
        val newEventId = (eventId ?: 0) + 1
        val newEventCommand =
            """
                INSERT INTO subscription_events (user_id, user_event_id, end_time)
                VALUES (?, ?, ?)
            """.trimIndent()
        transaction.sendPreparedStatement(newEventCommand, listOf(userId, newEventId, until))
        Unit
    }

    private suspend fun getNewId(transaction: SuspendingConnection): Int {
        for (retries in (0..5)) {
            val idsPool = idsPoolRef.get()
            if ((idsPool == null) || (idsPool.curMaxId == idsPool.maxAvailableId)) {
                val curMaxId = if (idsPool == null) {
                    val maxIdCommand =
                        """
                            SELECT max_id
                            FROM ids_pool
                            WHERE entity = 'USER';
                        """.trimIndent()
                    transaction.sendPreparedStatement(maxIdCommand).rows[0].getInt("max_id")!!
                } else {
                    idsPool.curMaxId
                }
                val newMaxAvailableId = curMaxId + poolSize
                val lendIdsCommand =
                    """
                        UPDATE ids_pool
                        SET max_id = ?
                        WHERE entity = 'USER'
                          AND max_id = ?
                    """.trimIndent()
                val lendStatus = transaction.sendPreparedStatement(lendIdsCommand, listOf(newMaxAvailableId, curMaxId))
                val newCurMaxId = curMaxId + 1
                val newIdsPool = IdsPool(newCurMaxId, newMaxAvailableId)
                if ((lendStatus.rowsAffected != 1L) && idsPoolRef.compareAndSet(idsPool, newIdsPool)) {
                    return newCurMaxId
                }
            } else {
                val newMaxId = idsPool.curMaxId + 1
                val newIdsPool = idsPool.copy(curMaxId = newMaxId)
                if (idsPoolRef.compareAndSet(idsPool, newIdsPool)) {
                    return newMaxId
                }
            }
        }
        throw IllegalStateException("Too many retries to get new id")
    }

    data class IdsPool(val curMaxId: Int, val maxAvailableId: Int)

    private val idsPoolRef = AtomicReference<IdsPool?>(null)
}
