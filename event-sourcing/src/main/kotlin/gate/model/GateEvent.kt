package gate.model

import org.joda.time.LocalDateTime

data class GateEvent(val type: GateEventType, val time: LocalDateTime)
