package common.connection

import com.github.jasync.sql.db.SuspendingConnection
import common.config.DatabaseConfig

interface ConnectionProvider {
    fun getConnection(databaseConfig: DatabaseConfig): SuspendingConnection
}
