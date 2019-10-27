package configs

import com.typesafe.config.Config
import java.time.Duration

data class VKVersion(val major: Int, val minor: Int)

class VKConfig(private val config: Config) {
    val schema: String
        get() = config.getString("schema")

    val host: String
        get() = config.getString("host")

    val port: Int
        get() = config.getInt("port")

    val accessToken: String
        get() = config.getString("access-token")

    val version: VKVersion
        get() {
            val versionConfig = config.getConfig("version")
            return VKVersion(versionConfig.getInt("major"), versionConfig.getInt("minor"))
        }

    val timeout: Duration
        get() = config.getDuration("timeout")
}