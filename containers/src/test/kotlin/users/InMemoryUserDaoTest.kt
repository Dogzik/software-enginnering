package users

import KFixedHostPortGenericContainer
import controller.ControllerException
import controller.TestExchangeController
import exchange.model.Shares
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import users.config.ExchangeClientConfig
import users.dao.InMemoryUsersDao
import users.http.KtorExchangeHttpClient
import users.model.FullUserShares

class InMemoryUserDaoTest {
    private val testClientConfig = ExchangeClientConfig("localhost", 8841)

    private var exchange: KFixedHostPortGenericContainer? = null
    private val client = KtorExchangeHttpClient(testClientConfig)
    private val controller = TestExchangeController(testClientConfig)
    private var dao = InMemoryUsersDao(client)

    @Before
    fun startExchange() {
        dao = InMemoryUsersDao(client)
        exchange = KFixedHostPortGenericContainer("dogzik/exchange:latest")
            .withFixedExposedPort(testClientConfig.port, 8080)
            .withExposedPorts(8080)
        exchange!!.start()
    }

    @After
    fun stopExchange() {
        exchange!!.stop()
    }

    @Test
    fun testAddUser() {
        val id = dao.addUser("bob")
        assertEquals(0, dao.getBalance(id))
    }

    @Test
    fun testTopUpBalance() {
        val id = dao.addUser("kek")
        assertEquals(0, dao.getBalance(id))
        dao.topUpBalance(id, 200)
        assertEquals(200, dao.getBalance(id))
    }

    @Test(expected = IllegalStateException::class)
    fun testSellingAbsentShares() = runBlocking {
        val id = dao.addUser("lol")
        dao.sellShares(id, "ibm", 10)
        Unit
    }

    @Test(expected = IllegalStateException::class)
    fun testBuyingNonExistingShares() = runBlocking {
        val id = dao.addUser("lol")
        dao.buyShares(id, "ibm", 2)
        Unit
    }

    @Test(expected = ControllerException::class)
    fun testChangingPriceForNonExistingCompany() = runBlocking {
        controller.changePrice("ibm", 20)
    }

    @Test
    fun testSimplePurchase() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        val debt = dao.buyShares(id, "ibm", 5)
        assertEquals(1000 - 5 * 10, dao.getBalance(id))
        assertEquals(5 * 10, debt)
    }

    @Test
    fun testDetailedShares() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        controller.addCompany("vk", Shares(10, 6))
        val detailedShares = HashSet<FullUserShares>()
        assertEquals(detailedShares, dao.getDetailedShares(id))
        dao.buyShares(id, "ibm", 5)
        detailedShares.add(FullUserShares("ibm", 5, 10))
        assertEquals(detailedShares, dao.getDetailedShares(id))
        dao.buyShares(id, "vk", 3)
        detailedShares.add(FullUserShares("vk", 3, 6))
        assertEquals(detailedShares, dao.getDetailedShares(id))
        Unit
    }

    @Test(expected = IllegalStateException::class)
    fun testBuyTooManyShares() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        dao.buyShares(id, "ibm", 25)
        Unit
    }

    @Test
    fun testSimpleSellShare() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        dao.buyShares(id, "ibm", 5)
        assertEquals(1000 - 5 * 10, dao.getBalance(id))
        val profit = dao.sellShares(id, "ibm", 3)
        assertEquals(3 * 10, profit)
        assertEquals(1000 - 2 * 10, dao.getBalance(id))
        assertEquals(1000, dao.getTotalBalance(id))
    }

    @Test(expected = IllegalStateException::class)
    fun testSellTooMuchShares() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        dao.buyShares(id, "ibm", 5)
        dao.sellShares(id, "ibm", 7)
        Unit
    }

    @Test
    fun testChangingPrice() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        dao.buyShares(id, "ibm", 5)
        controller.changePrice("ibm", 20)
        assertEquals(setOf(FullUserShares("ibm", 5, 20)), dao.getDetailedShares(id))
        assertEquals(1000 + 5 * 10, dao.getTotalBalance(id))
    }

    @Test
    fun testServerErrorDuringSelling() = runBlocking {
        val id = dao.addUser("user")
        dao.topUpBalance(id, 1000)
        controller.addCompany("ibm", Shares(10, 10))
        dao.buyShares(id, "ibm", 5)
        exchange!!.pause()
        try {
            dao.sellShares(id, "ibm", 2)
            fail("Shouldn't reach here")
        } catch (e: IllegalStateException) {
            exchange!!.unpause()
            assertEquals(1000 - 5 * 10, dao.getBalance(id))
            assertEquals(setOf(FullUserShares("ibm", 5, 10)), dao.getDetailedShares(id))
        }
    }
}
