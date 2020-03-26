package users

import com.typesafe.config.ConfigFactory
import getCompany
import getCount
import getId
import getName
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import users.config.ExchangeClientConfig
import users.dao.InMemoryUsersDao
import users.http.KtorExchangeHttpClient
import java.nio.file.Paths
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.serialization.builtins.set
import respondError
import respondNotEnoughParams
import users.model.FullUserShares

fun main(): Unit = runBlocking {
    val configFile = Paths.get("src/main/resources/users.conf").toFile()
    val config = ConfigFactory.parseFile(configFile)
    val clientConfig = ExchangeClientConfig.fromConfig(config.getConfig("client"))
    val serverConfig = config.getConfig("server")
    val client = KtorExchangeHttpClient(clientConfig)
    val dao = InMemoryUsersDao(client)
    val parser = Json(JsonConfiguration.Stable)
    embeddedServer(Netty, port = serverConfig.getInt("port")) {
        routing {
            get("/add_user") {
                val name = call.getName()
                if (name == null) {
                    call.respondNotEnoughParams()
                } else {
                    val id = dao.addUser(name)
                    call.respondText("Added user with id = $id")
                }
            }
            get("/top_up_balance") {
                val id = call.getId()
                val count = call.getCount()
                if ((id == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    dao.topUpBalance(id, count)
                    call.respondText("Successfully topped up")
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_balance") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val balance = dao.getBalance(id)
                    call.respondText(balance.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_detailed_shares") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val detailedShares = dao.getDetailedShares(id)
                    call.respondText(parser.stringify(FullUserShares.serializer().set, detailedShares))
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/get_total_balance") {
                val id = call.getId()
                if (id == null) {
                    call.respondNotEnoughParams()
                } else try {
                    val balance = dao.getTotalBalance(id)
                    call.respondText(balance.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/buy_shares") {
                val id = call.getId()
                val company = call.getCompany()
                val count = call.getCount()
                if ((id == null) || (company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val debt = dao.buyShares(id, company, count)
                    call.respondText(debt.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
            get("/sell_shares") {
                val id = call.getId()
                val company = call.getCompany()
                val count = call.getCount()
                if ((id == null) || (company == null) || (count == null)) {
                    call.respondNotEnoughParams()
                } else try {
                    val profit = dao.sellShares(id, company, count)
                    call.respondText(profit.toString())
                } catch (e: Exception) {
                    call.respondError(e)
                }
            }
        }
    }.start(wait = true)
    Unit
}
