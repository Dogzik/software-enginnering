package manager.model

import org.joda.time.LocalDateTime

data class User(val user_id: Int, val name: String, val subscriptionEnd: LocalDateTime?)
