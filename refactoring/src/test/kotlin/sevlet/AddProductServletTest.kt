package sevlet

import dao.ProductWriteDao
import model.Product
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import servlet.AddProductServlet
import javax.servlet.http.HttpServletRequest

class AddProductsServletTest {
    @Test
    fun testAdd() {
        val product = Product("product", 1121)
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductWriteDao::class.java)
        `when`(request.getParameter("name")).thenReturn(product.name)
        `when`(request.getParameter("price")).thenReturn(product.price.toString())
        doNothing().`when`(dao).addProduct(product)
        val servlet = AddProductServlet(dao)
        val result = servlet.processRequest(request)
        verify(dao, times(1)).addProduct(product)
        assertEquals(result, "OK")
    }
}