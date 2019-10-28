package servlet

import dao.ProductReadDao
import response.ResponseBuilder
import javax.servlet.http.HttpServletRequest

class GetAllProductsServlet(private val productsReadDao: ProductReadDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        val responseBuilder = ResponseBuilder()
        productsReadDao.getAllProducts().forEach {
            responseBuilder.addResponseElement("${it.name}\t${it.price}")
        }
        return responseBuilder.buildAnswer()
    }
}
