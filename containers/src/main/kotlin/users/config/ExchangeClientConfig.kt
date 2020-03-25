package users.config

import com.typesafe.config.Config

data class ExchangeClientConfig(val host: String, val port: Int) {
    companion object {
        fun fromConfig(config: Config) = ExchangeClientConfig(
            config.getString("host"),
            config.getInt("port")
        )
    }
}
