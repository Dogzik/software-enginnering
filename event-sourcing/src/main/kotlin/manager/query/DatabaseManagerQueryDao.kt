package manager.query

import com.github.jasync.sql.db.SuspendingConnection
import common.dao.CommonDao
import manager.model.User

class DatabaseManagerQueryDao(private val connection: SuspendingConnection) : ManagerQueryDao, CommonDao() {
    override suspend fun getUser(userId: Int): User? =
        connection.inTransaction { getUserWithSubscription(it, userId).first }
}
