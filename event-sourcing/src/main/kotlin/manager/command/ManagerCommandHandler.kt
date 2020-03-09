package manager.command

import common.Handler

class ManagerCommandHandler(private val dao: ManagerCommandDao) : Handler<ManagerCommand> {
    override suspend fun doHandle(task: ManagerCommand): String =
        when (task) {
            is CreateUserCommand -> {
                val id = dao.createUser(task.name)
                "New user's id = $id"
            }
            is RenewSubscriptionCommand -> {
                dao.renewSubscription(task.user_id, task.until)
                "Successfully renewed subscription"
            }
        }

}
