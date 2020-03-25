package exchange.model

import kotlinx.serialization.Serializable

@Serializable
data class SharesPurchase(val count: Long, val debt: Long)
