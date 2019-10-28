package dao

import config.SQLCommandsImpl
import connection.ConnectionProvider
import model.Product
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import java.nio.file.Paths
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class ProductDaoImplTest {
    private val dbCommands = SQLCommandsImpl(Paths.get("src/main/sql"))

    @Test
    fun testCount() {
        val cnt = 20
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getCountProducts)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getInt(1)).thenReturn(cnt)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getCount()
        assertEquals(result, cnt)
    }

    @Test
    fun testPricesSum() {
        val sum = 2169L
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getSumPrices)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getLong(1)).thenReturn(sum)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getSumPrices()
        assertEquals(result, sum)
    }

    @Test
    fun testCreateTable() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        `when`(statement.executeUpdate(dbCommands.createDB)).thenReturn(0)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        dao.createProductsTable()
        verify(statement, times(1)).executeUpdate(dbCommands.createDB)
    }

    @Test
    fun testMin() {
        val minProduct = Product("product", 322)
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getMinPriceProduct)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getString("name")).thenReturn(minProduct.name)
        `when`(resultSet.getInt("price")).thenReturn(minProduct.price)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getMinPriceProduct()
        assertEquals(result, minProduct)
    }

    @Test
    fun testMax() {
        val maxProduct = Product("product", 1337)
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getMaxPriceProduct)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true)
        `when`(resultSet.getString("name")).thenReturn(maxProduct.name)
        `when`(resultSet.getInt("price")).thenReturn(maxProduct.price)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getMaxPriceProduct()
        assertEquals(result, maxProduct)
    }

    @Test
    fun testMinEmpty() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getMinPriceProduct)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getMinPriceProduct()
        assertEquals(result, null)
    }

    @Test
    fun testMaxEmpty() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getMaxPriceProduct)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getMaxPriceProduct()
        assertEquals(result, null)
    }

    @Test
    fun testAdd() {
        val product = Product("product", 14)
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val preparedStatement = mock(PreparedStatement::class.java)
        `when`(connection.prepareStatement(dbCommands.insertProduct)).thenReturn(preparedStatement)
        doNothing().`when`(preparedStatement).setString(1, product.name)
        doNothing().`when`(preparedStatement).setInt(2, product.price)
        `when`(preparedStatement.executeUpdate()).thenReturn(0)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        dao.addProduct(product)
        verify(preparedStatement, times(1)).setString(1, product.name)
        verify(preparedStatement, times(1)).setInt(2, product.price)
    }

    @Test
    fun testGetAllProducts() {
        val product1 = Product("product1", 25)
        val product2 = Product("product2", 17)
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getAllProducts)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false)
        `when`(resultSet.getString("name"))
            .thenReturn(product1.name)
            .thenReturn(product2.name)
        `when`(resultSet.getInt("price"))
            .thenReturn(product1.price)
            .thenReturn(product2.price)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getAllProducts()
        assertEquals(result.toList(), listOf(product1, product2))
    }

    @Test
    fun testGetEmptyProductsList() {
        val connectionProvider = mock(ConnectionProvider::class.java)
        val connection = mock(Connection::class.java)
        `when`(connectionProvider.getConnection()).thenReturn(connection)
        val statement = mock(Statement::class.java)
        `when`(connection.createStatement()).thenReturn(statement)
        val resultSet = mock(ResultSet::class.java)
        `when`(statement.executeQuery(dbCommands.getAllProducts)).thenReturn(resultSet)
        `when`(resultSet.next()).thenReturn(false)
        val dao = ProductDaoImpl(dbCommands, connectionProvider)
        val result = dao.getAllProducts()
        assertEquals(result.toList(), listOf<Product>())
    }
}