package users.model

import kotlinx.serialization.Serializable

@Serializable
data class FullUserShares(val company: String, val count: Long, val price: Long)
