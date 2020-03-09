package manager.config

import com.typesafe.config.Config
import common.config.DatabaseConfig
import common.config.ServerConfig

data class MangerConfig(
    val serverConfig: ServerConfig,
    val databaseConfig: DatabaseConfig
) {
    companion object {
        fun fromConfig(config: Config) = MangerConfig(
            ServerConfig.fromConfig(config.getConfig("server")),
            DatabaseConfig.fromConfig(config.getConfig("database"))
        )
    }
}
