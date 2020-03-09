package manager.command

import org.joda.time.LocalDateTime

interface ManagerCommandDao {
    suspend fun createUser(name: String): Int

    suspend fun renewSubscription(user_id: Int, until: LocalDateTime)
}
