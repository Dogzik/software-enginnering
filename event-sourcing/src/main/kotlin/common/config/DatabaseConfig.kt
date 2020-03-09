package common.config

import com.typesafe.config.Config

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val maxConnections: Int
) {
    companion object {
        fun fromConfig(config: Config) = DatabaseConfig(
            config.getString("host"),
            config.getInt("port"),
            config.getString("database"),
            config.getString("username"),
            config.getString("password"),
            config.getInt("maxConnections")
        )
    }
}