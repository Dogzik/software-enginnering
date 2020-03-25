package users.dao

import exchange.model.SharesPurchase
import users.model.FullUserShares

interface UsersDao {
    fun addUser(name: String): Long
    fun topUpBalance(id: Long, count: Long)
    fun getBalance(id: Long): Long
    suspend fun getDetailedShares(id: Long): List<FullUserShares>
    suspend fun getTotalBalance(id: Long): Long
    suspend fun buyShares(id: Long, company: String, count: Long): SharesPurchase
    suspend fun sellShares(id: Long, company: String, count: Long): Long
}
