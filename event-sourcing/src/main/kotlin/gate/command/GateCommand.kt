package gate.command

import org.joda.time.LocalDateTime

sealed class GateCommand

data class EnterCommand(val userId: Int, val time: LocalDateTime) : GateCommand()

data class ExitCommand(val userId: Int, val time: LocalDateTime) : GateCommand()
