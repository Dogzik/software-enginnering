import com.typesafe.config.ConfigFactory
import config.ApplicationConfigImpl
import connection.ConnectionProviderImpl
import java.io.File
import java.nio.file.Paths
import config.SQLCommandsImpl
import dao.ProductDaoImpl
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import servlet.AddProductServlet
import servlet.GetAllProductsServlet
import servlet.QueryServlet
import javax.servlet.http.HttpServlet

fun main() {
    val config = ApplicationConfigImpl(
        ConfigFactory.parseFile(File("src/main/resources/application.conf"))
    )
    val sqlCommands = SQLCommandsImpl(Paths.get("src/main/sql"))
    val connectionProvider = ConnectionProviderImpl(config.database)
    val productsDao = ProductDaoImpl(sqlCommands, connectionProvider)
    productsDao.createProductsTable()
    val server = Server(config.port)
    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.contextPath = "/"
    server.handler = context
    val servlets = mapOf<String, HttpServlet>(
        Pair("/add-product", AddProductServlet(productsDao)),
        Pair("/get-products", GetAllProductsServlet(productsDao)),
        Pair("/query", QueryServlet(productsDao))
    )
    servlets.forEach { (path, servlet) ->
        context.addServlet(ServletHolder(servlet), path)
    }
    server.start()
    server.join()
}