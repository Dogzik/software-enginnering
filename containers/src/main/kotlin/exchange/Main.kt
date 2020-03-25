package exchange

import exchange.dao.InMemoryExchangeDao
import exchange.model.Shares
import exchange.model.SharesPurchase
import getCompany
import getCount
import getPrice
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import respondError
import respondNotEnoughParams
import kotlin.Exception


fun main(): Unit = runBlocking {
    val dao = InMemoryExchangeDao()
    val parser = Json(JsonConfiguration.Stable)
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/add_company") {
                val company = call.getCompany()
                val count = call.getCount()
                val price = call.getPrice()
                if ((company == null) || (count == null) || (price == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.addCompany(company, Shares(count, price))
                    call.respondText("Added new company")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/add_shares") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.addShares(company, count)
                    call.respondText("Added shares")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_shares") {
                val company = call.getCompany()
                if (company == null) {
                    call.respondNotEnoughParams()
                } else {
                    val shares = dao.getShares(company)
                    val response = shares?.let { parser.stringify(Shares.serializer(), shares) } ?: ""
                    call.respondText(response)
                }
            }
            get("/buy_shares") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val purchase = dao.buyShares(company, count)
                    call.respondText(parser.stringify(SharesPurchase.serializer(), purchase))
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/sell_shares") {
                val company = call.getCompany()
                val count = call.getCount()
                if ((company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val profit = dao.sellShares(company, count)
                    call.respondText(profit.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/change_price") {
                val company = call.getCompany()
                val price = call.getPrice()
                if ((company == null) || (price == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.changePrice(company, price)
                    call.respondText("Price changed")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
        }
    }.start(wait = true)
    Unit
}
