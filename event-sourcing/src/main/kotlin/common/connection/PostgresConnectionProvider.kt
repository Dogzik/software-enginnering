package common.connection

import com.github.jasync.sql.db.SuspendingConnection
import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder
import common.config.DatabaseConfig

object PostgresConnectionProvider : ConnectionProvider {
    override fun getConnection(databaseConfig: DatabaseConfig): SuspendingConnection {
        return PostgreSQLConnectionBuilder.createConnectionPool {
            host = databaseConfig.host
            port = databaseConfig.port
            database = databaseConfig.database
            username = databaseConfig.username
            password = databaseConfig.password
            maxActiveConnections = databaseConfig.maxConnections
        }.asSuspending
    }
}
