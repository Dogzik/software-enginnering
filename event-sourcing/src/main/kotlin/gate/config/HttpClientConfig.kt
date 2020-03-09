package gate.config

import com.typesafe.config.Config

data class HttpClientConfig(val schema: String, val host: String, val port: Int) {
    companion object {
        fun fromConfig(config: Config) = HttpClientConfig(
            config.getString("schema"),
            config.getString("host"),
            config.getInt("port")
        )
    }
}
