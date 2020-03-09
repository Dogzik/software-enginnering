package manager.query

import common.Handler

class ManagerQueryHandler(private val dao: ManagerQueryDao) : Handler<ManagerQuery> {
    override suspend fun doHandle(task: ManagerQuery): String =
        when (task) {
            is GetUserQuery -> dao.getUser(task.user_id)?.toString() ?: "No such user"
        }
}
