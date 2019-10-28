package servlet

import dao.ProductReadDao
import command.maxPriceCommand
import command.minPriceCommand
import command.pricesSumCommand
import command.countCommand
import javax.servlet.http.HttpServletRequest

class QueryServlet(private val productsReadDao: ProductReadDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        return when (val command = request.getParameter("command")) {
            "max" -> maxPriceCommand(productsReadDao)
            "min" -> minPriceCommand(productsReadDao)
            "sum" -> pricesSumCommand(productsReadDao)
            "count" -> countCommand(productsReadDao)
            else -> "Unknown command: $command"
        }
    }
}