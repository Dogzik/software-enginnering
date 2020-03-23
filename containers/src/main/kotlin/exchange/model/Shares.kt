package exchange.model

import kotlinx.serialization.Serializable

@Serializable
data class Shares(val count: Long, val price: Long)
