package manager.command

import org.joda.time.LocalDateTime

sealed class ManagerCommand

data class CreateUserCommand(val name: String) : ManagerCommand()

data class RenewSubscriptionCommand(val user_id: Int, val until: LocalDateTime) : ManagerCommand()
