package gate.config

import com.typesafe.config.Config
import common.config.DatabaseConfig
import common.config.ServerConfig

data class GateConfig(
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig,
    val httpClientConfig: HttpClientConfig
) {
    companion object {
        fun fromConfig(config: Config) = GateConfig(
            ServerConfig.fromConfig(config.getConfig("server")),
            DatabaseConfig.fromConfig(config.getConfig("database")),
            HttpClientConfig.fromConfig(config.getConfig("httpClient"))
        )
    }
}
