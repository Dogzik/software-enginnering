package sevlet

import dao.ProductReadDao
import model.Product
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import servlet.GetAllProductsServlet
import javax.servlet.http.HttpServletRequest

class GetProductsServletTest {
    @Test
    fun testGet() {
        val product1 = Product("product1", 2517)
        val product2 = Product("product2", 1337)
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getAllProducts())
            .thenReturn(listOf(product1, product2))
        val servlet = GetAllProductsServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "   ${product1.name} ${product1.price}\n" +
                    "  <br> ${product2.name} ${product2.price}\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testEmptyGet() {
        val request = mock(HttpServletRequest::class.java)
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getAllProducts()).thenReturn(listOf())
        val servlet = GetAllProductsServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }
}