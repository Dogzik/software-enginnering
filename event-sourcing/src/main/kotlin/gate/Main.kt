package gate

import com.typesafe.config.ConfigFactory
import common.connection.PostgresConnectionProvider
import common.utils.getUserId
import gate.command.DatabaseGateCommandDao
import gate.command.EnterCommand
import gate.command.ExitCommand
import gate.command.GateCommandHandler
import gate.config.GateConfig
import gate.http.KtorStatsHttpClientProvider
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import java.nio.file.Paths

fun main() = runBlocking {
    val configFile = Paths.get("src/main/resources/gate.conf").toFile()
    val config = GateConfig.fromConfig(ConfigFactory.parseFile(configFile))
    val connection = PostgresConnectionProvider.getConnection(config.databaseConfig)
    val provider = KtorStatsHttpClientProvider(config.httpClientConfig)
    val commandDao = DatabaseGateCommandDao(connection)
    val commandHandler = GateCommandHandler(commandDao, provider)
    val server = embeddedServer(Netty, port = config.serverConfig.port) {
        routing {
            get("/command/enter") {
                val userId = getUserId(call.request)
                if (userId == null) {
                    call.respondText("Error: user_id is required", status = HttpStatusCode.BadRequest)
                } else {
                    val enterTime = LocalDateTime.now()
                    val command = EnterCommand(userId, enterTime)
                    call.respondText(commandHandler.handle(command))
                }
            }
            get("/command/exit") {
                val userId = getUserId(call.request)
                if (userId == null) {
                    call.respondText("Error: user_id is required", status = HttpStatusCode.BadRequest)
                } else {
                    val enterTime = LocalDateTime.now()
                    val command = ExitCommand(userId, enterTime)
                    call.respondText(commandHandler.handle(command))
                }
            }
        }
    }.start(wait = true)
}
