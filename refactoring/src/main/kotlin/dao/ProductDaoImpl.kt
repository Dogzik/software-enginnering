package dao

import config.SQLCommands
import connection.ConnectionProvider
import model.Product
import java.sql.ResultSet

class ProductDaoImpl(private val dbCommands: SQLCommands,
                     private val connectionProvider: ConnectionProvider) :
    ProductReadDao, ProductWriteDao {

    private fun <T> getScalarResult(SQLQuery: String, resultFetcher: ResultSet.(Int) -> T): T {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(SQLQuery).use { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.resultFetcher(1)
                    } else {
                        throw Exception("Scalar query failed")
                    }
                }
            }
        }
    }

    private fun getOptionalProduct(SQLQuery: String): Product? {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(SQLQuery).use { resultSet ->
                    return if (resultSet.next()) {
                        val name = resultSet.getString("name")
                        val price = resultSet.getInt("price")
                        Product(name, price)
                    } else {
                        null
                    }
                }
            }
        }
    }

    override fun getCount(): Int = getScalarResult(dbCommands.getCountProducts, ResultSet::getInt)

    override fun getSumPrices(): Long = getScalarResult(dbCommands.getSumPrices, ResultSet::getLong)

    override fun getAllProducts(): List<Product> {
        val result = mutableListOf<Product>()
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(dbCommands.getAllProducts).use { resultSet ->
                    while (resultSet.next()) {
                        val name = resultSet.getString("name")
                        val price = resultSet.getInt("price")
                        result.add(Product(name, price))
                    }
                }
            }
        }
        return result
    }

    override fun getMinPriceProduct(): Product? = getOptionalProduct(dbCommands.getMinPriceProduct)

    override fun getMaxPriceProduct(): Product? = getOptionalProduct(dbCommands.getMaxPriceProduct)

    override fun addProduct(product: Product) {
        connectionProvider.getConnection().use { connection ->
            connection.prepareStatement(dbCommands.insertProduct).use { statement ->
                statement.setString(1, product.name)
                statement.setInt(2, product.price)
                statement.executeUpdate()
            }
        }
    }

    override fun createProductsTable() {
        connectionProvider.getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(dbCommands.createDB)
            }
        }
    }
}