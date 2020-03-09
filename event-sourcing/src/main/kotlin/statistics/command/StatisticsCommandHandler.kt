package statistics.command

import common.Handler

class StatisticsCommandHandler(private val dao: StatisticsCommandDao) : Handler<StatisticsCommand> {
    override suspend fun doHandle(task: StatisticsCommand): String =
        when (task) {
            is AddVisitCommand -> {
                dao.addVisit(task.userId, task.startTime, task.endTime, task.eventId)
                "OK"
            }
        }
}
