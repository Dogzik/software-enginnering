package exchange.dao

import exchange.model.Shares
import java.util.concurrent.ConcurrentHashMap

class InMemoryExchangeDao : ExchangeDao {
    override fun addCompany(company: String, shares: Shares) {
        require((shares.count > 0) || (shares.price > 0)) { "Count and price must be positive" }
        check(companies.putIfAbsent(company, shares) == null) { "Company $company already exists" }
    }

    override fun addShares(company: String, count: Long) {
        require(count > 0) { "Added count must positive" }
        val newShares = companies.computeIfPresent(company) { _, shares -> shares.copy(count = shares.count + count) }
        check(newShares != null) { "Company $company doesn't exists" }
    }

    override fun getShares(company: String): Shares? = companies[company]

    override fun buyShares(company: String, count: Long): Long {
        require(count > 0) { "Count must be positive" }
        var debt: Long? = null
        val newShares = companies.computeIfPresent(company) { _, shares ->
            if (shares.count < count) {
                shares
            } else {
                debt = count * shares.price
                shares.copy(count = shares.count - count)
            }
        }
        check(newShares != null) { "Company $company doesn't exists" }
        check(debt != null) { "Doesn't have enough shares" }
        return debt!!
    }

    override fun sellShares(company: String, count: Long): Long {
        require(count > 0) { "Count must be positive" }
        var profit = 0L
        val newShares = companies.computeIfPresent(company) { _, shares ->
            profit = count * shares.price
            shares.copy(count = shares.count + count)
        }
        check(newShares != null) { "Company $company doesn't exists" }
        return profit
    }

    override fun changePrice(company: String, newPrice: Long) {
        require(newPrice > 0) { "Price must be positive" }
        val newShares = companies.computeIfPresent(company) { _, shares -> shares.copy(price = newPrice) }
        check(newShares != null)
    }

    private val companies = ConcurrentHashMap<String, Shares>()
}
