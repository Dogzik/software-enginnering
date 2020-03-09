package manager

import com.typesafe.config.ConfigFactory
import common.connection.PostgresConnectionProvider
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import manager.command.CreateUserCommand
import manager.command.DatabaseManagerCommandDao
import manager.command.ManagerCommandHandler
import manager.command.RenewSubscriptionCommand
import manager.config.MangerConfig
import manager.query.DatabaseManagerQueryDao
import manager.query.GetUserQuery
import manager.query.ManagerQueryHandler
import org.joda.time.LocalDateTime
import java.nio.file.Paths

fun main() = runBlocking {
    val configFile = Paths.get("src/main/resources/manager.conf").toFile()
    val config = MangerConfig.fromConfig(ConfigFactory.parseFile(configFile))
    val connection = PostgresConnectionProvider.getConnection(config.databaseConfig)
    val queryDao = DatabaseManagerQueryDao(connection)
    val commandDao = DatabaseManagerCommandDao(connection)
    val queryHandler = ManagerQueryHandler(queryDao)
    val commandHandler = ManagerCommandHandler(commandDao)
    var server = embeddedServer(Netty, port = config.serverConfig.port) {
        routing {
            get("/command/renew_sub") {
                val userId = call.request.queryParameters["user_id"]?.toInt()
                val until = call.request.queryParameters["until"]?.let { LocalDateTime.parse(it) }
                if ((userId == null) || (until == null)) {
                    call.respondText("Error: user_id and until are required", status = HttpStatusCode.BadRequest);
                } else {
                    val command = RenewSubscriptionCommand(userId, until)
                    call.respondText(commandHandler.handle(command))
                }
            }
            get("/command/create_user") {
                val name = call.request.queryParameters["name"]
                if (name == null) {
                    call.respondText("Error: name is required", status = HttpStatusCode.BadRequest)
                } else {
                    val command = CreateUserCommand(name)
                    call.respondText(commandHandler.handle(command))
                }
            }
            get("/query/get_user") {
                val userId = call.request.queryParameters["user_id"]?.toInt()
                if (userId == null) {
                    call.respondText("Error: user_id is required", status = HttpStatusCode.BadRequest)
                } else {
                    val query = GetUserQuery(userId)
                    call.respondText(queryHandler.handle(query))
                }
            }
        }
    }.start(wait = true)
}
