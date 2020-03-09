package common.config

import com.typesafe.config.Config

data class ServerConfig(val port: Int) {
    companion object {
        fun fromConfig(config: Config) = ServerConfig(config.getInt("port"))
    }
}