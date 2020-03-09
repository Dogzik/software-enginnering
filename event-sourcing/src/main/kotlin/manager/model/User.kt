package manager.model

import org.joda.time.LocalDateTime

data class User(val userId: Int, val name: String, val subscriptionEnd: LocalDateTime?)
