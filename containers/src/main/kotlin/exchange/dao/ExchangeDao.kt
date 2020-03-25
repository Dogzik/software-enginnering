package exchange.dao

import exchange.model.Shares
import exchange.model.SharesPurchase

interface ExchangeDao {
    fun addCompany(company: String, shares: Shares)
    fun addShares(company: String, count: Long)
    fun getShares(company: String): Shares?
    fun buyShares(company: String, count: Long): SharesPurchase
    fun sellShares(company: String, count: Long): Long
    fun changePrice(company: String, newPrice: Long)
}
