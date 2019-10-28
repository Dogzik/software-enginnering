package connection

import java.sql.Connection
import java.sql.DriverManager

interface ConnectionProvider {
    fun getConnection(): Connection
}

class ConnectionProviderImpl(private val connectionName: String) : ConnectionProvider {
    override fun getConnection(): Connection {
        return DriverManager.getConnection(connectionName)
    }
}