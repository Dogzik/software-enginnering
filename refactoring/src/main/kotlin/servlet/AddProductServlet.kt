package servlet

import model.Product

import dao.ProductWriteDao
import javax.servlet.http.HttpServletRequest

class AddProductServlet(private val productsWriteDao: ProductWriteDao) : ProductsProcessingServlet() {
    override fun processRequest(request: HttpServletRequest): String {
        val name = request.getParameter("name")
        val price = Integer.parseInt(request.getParameter("price"))
        productsWriteDao.addProduct(Product(name, price))
        return "OK"
    }
}