package users.dao

import users.http.ExchangeHttpClient
import users.model.FullUserShares
import users.model.User
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class InMemoryUsersDao(private val client: ExchangeHttpClient) : UsersDao {
    override fun addUser(name: String): Long {
        while (true) {
            val newId = users.size.toLong()
            val putRes = users.putIfAbsent(newId, User(name, 0, HashMap()))
            @Suppress("FoldInitializerAndIfToElvis")
            if (putRes == null) {
                return newId
            }
        }
    }

    override fun topUpBalance(id: Long, count: Long) {
        require(count > 0) { "Count must be positive" }
        val newBalance = users.computeIfPresent(id) { _, user -> user.copy(balance = user.balance + count) }
        check(newBalance != null) { "User with id $id doesn't exist" }
    }

    override suspend fun getDetailedShares(id: Long): Set<FullUserShares> {
        val user = users[id]
        check(user != null) { "User with id $id doesn't exist" }
        return getUserDetailedShares(user).toSet()
    }

    override fun getBalance(id: Long): Long {
        val user = users[id]
        check(user != null) { "User with id $id doesn't exist" }
        return user.balance
    }

    override suspend fun getTotalBalance(id: Long): Long {
        val user = users[id]
        check(user != null) { "User with id $id doesn't exist" }
        val detailedShares = getUserDetailedShares(user)
        val sharesBalance = detailedShares.map { it.count * it.price }.sum()
        return sharesBalance + user.balance
    }

    override suspend fun buyShares(id: Long, company: String, count: Long): Long {
        check(users.containsKey(id)) { "User with id $id doesn't exist" }
        val debt = client.buyShares(company, count)
        users.computeIfPresent(id) { _, user ->
            val mutableShares = user.shares.toMutableMap()
            mutableShares.compute(company) { _, cnt -> cnt?.plus(count) ?: count }
            user.copy(balance = user.balance - debt, shares = mutableShares)
        }
        return debt
    }

    override suspend fun sellShares(id: Long, company: String, count: Long): Long {
        var success = false
        users.computeIfPresent(id) { _, user ->
            if (user.shares[company]?.let { it < count } != false) {
                success = false
                user
            } else {
                success = true
                val mutableShares = user.shares.toMutableMap()
                mutableShares.computeIfPresent(company) { _, curCount -> curCount - count }
                user.copy(shares = mutableShares)
            }
        }
        check(success) { "Not enough shares to sell or no user" }
        try {
            val profit = client.sellShares(company, count)
            users.computeIfPresent(id) { _, user -> user.copy(balance = user.balance + profit) }
            return profit
        } catch (e: Exception) {
            users.computeIfPresent(id) { _, user ->
                val mutableShares = user.shares.toMutableMap()
                mutableShares.computeIfPresent(company) { _, curCount -> curCount + count }
                user.copy(shares = mutableShares)
            }
            throw e
        }
    }

    private suspend fun getUserDetailedShares(user: User): List<FullUserShares> {
        val userShares = user.shares.toList()
        val userCompanies = userShares.map { it.first }
        val prices = client.getSharesPrices(userCompanies)
        return userShares.zip(prices).map { FullUserShares(it.first.first, it.first.second, it.second) }
    }

    private val users = ConcurrentHashMap<Long, User>()
}
