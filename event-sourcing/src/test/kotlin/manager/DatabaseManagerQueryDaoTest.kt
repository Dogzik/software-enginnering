package manager

import com.github.jasync.sql.db.SuspendingConnection
import getNoUserConnection
import getUserSubscriptionConnection
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import manager.model.User
import manager.query.DatabaseManagerQueryDao
import org.joda.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseManagerQueryDaoTest {
    @Test
    fun testNoUser() = runBlocking {
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> User?>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> User?
                val transaction = getNoUserConnection(1)
                callback(transaction)
            }
        val dao = DatabaseManagerQueryDao(connection)
        assertEquals(null, dao.getUser(1))
    }

    @Test
    fun testSomeUser() = runBlocking {
        val until = LocalDateTime.parse("2020-10-11")
        val name = "Bob"
        val id = 1
        val connection = mockk<SuspendingConnection>()
        coEvery { connection.inTransaction(any<suspend (SuspendingConnection) -> User?>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> User?
                val transaction = getUserSubscriptionConnection(id, name, 11, until)
                callback(transaction)
            }
        val dao = DatabaseManagerQueryDao(connection)
        assertEquals(User(id, name, until), dao.getUser(id))
    }
}
