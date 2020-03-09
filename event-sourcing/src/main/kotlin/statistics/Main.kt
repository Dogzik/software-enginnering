package statistics

import com.typesafe.config.ConfigFactory
import common.connection.PostgresConnectionProvider
import common.utils.getUserId
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import statistics.command.AddVisitCommand
import statistics.command.StatisticsCommandHandler
import statistics.command.StatisticsStateCommandDao
import statistics.config.StatisticsConfig
import statistics.query.StatisticsQueryHandler
import statistics.query.StatisticsStateQueryDao
import statistics.query.UserStatisticsQuery
import statistics.state.MemoryStatisticsState
import java.nio.file.Paths

fun main() = runBlocking {
    val configFile = Paths.get("src/main/resources/statistics.conf").toFile()
    val config = StatisticsConfig.fromConfig(ConfigFactory.parseFile(configFile))
    val connection = PostgresConnectionProvider.getConnection(config.databaseConfig)
    val state = MemoryStatisticsState()
    state.init(connection)
    val queryDao = StatisticsStateQueryDao(state)
    val commandDao = StatisticsStateCommandDao(state)
    val queryHandler = StatisticsQueryHandler(queryDao)
    val commandHandler = StatisticsCommandHandler(commandDao)
    val sever = embeddedServer(Netty, port = config.serverConfig.port) {
        routing {
            get("/query/user_stats") {
                val userId = getUserId(call.request)
                if (userId == null) {
                    call.respondText("Error: user_id is required", status = HttpStatusCode.BadRequest)
                } else {
                    val query = UserStatisticsQuery(userId)
                    call.respondText(queryHandler.handle(query))
                }
            }
            get("/command/add_visit") {
                val userId = getUserId(call.request)
                val startTime = call.request.queryParameters["start_time"]?.let { LocalDateTime.parse(it) }
                val endTime = call.request.queryParameters["end_time"]?.let { LocalDateTime.parse(it) }
                val eventId = call.request.queryParameters["event_id"]?.toInt()
                if ((userId == null) || (startTime == null) || (endTime == null) || (eventId == null)) {
                    call.respondText(
                        "Error: user_id, start_time and end_time are required",
                        status = HttpStatusCode.BadRequest
                    )
                } else {
                    call.respondText(commandHandler.handle(AddVisitCommand(userId, startTime, endTime, eventId)))
                }
            }
        }
    }.start(wait = true)
}
