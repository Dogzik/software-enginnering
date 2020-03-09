package gate.command

import common.Handler
import gate.http.StatsHttpClientProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GateCommandHandler(
    private val dao: GateCommandDao,
    private val provider: StatsHttpClientProvider
) : Handler<GateCommand> {
    override suspend fun doHandle(task: GateCommand): String =
        when (task) {
            is EnterCommand -> {
                dao.processEnter(task.userId, task.time)
                "Entering center.."
            }
            is ExitCommand -> {
                val (startTime, eventId) = dao.processExit(task.userId, task.time)
                val request = "user_id = ${task.userId}, start_time = $startTime" +
                        ", end_time = ${task.time}, event_id = $eventId"
                val client = provider.getClient()
                GlobalScope.launch {
                    val response = try {
                        client.sendVisit(task.userId, startTime, task.time, eventId)
                    } catch (e: Exception) {
                        "ERROR: ${e.message}"
                    }
                    System.err.println("$request: $response")
                }
                "Exiting center.."
            }
        }
}
