package gate.command

import org.joda.time.LocalDateTime

interface GateCommandDao {
    /**
     * Returns corresponding enter time
     */
    suspend fun processExit(userId: Int, time: LocalDateTime): Pair<LocalDateTime, Int>

    suspend fun processEnter(userId: Int, time: LocalDateTime)
}
