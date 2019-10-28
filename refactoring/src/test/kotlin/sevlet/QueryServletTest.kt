package sevlet

import dao.ProductReadDao
import model.Product
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import servlet.QueryServlet
import javax.servlet.http.HttpServletRequest

class QueryServletTest {
    @Test
    fun testMin() {
        val minProduct = Product("product", 10)
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("min")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getMinPriceProduct()).thenReturn(minProduct)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with min price: </h1> ${minProduct.name} ${minProduct.price}\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMinNull() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("min")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getMinPriceProduct()).thenReturn(null)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with min price: </h1> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMax() {
        val maxProduct = Product("product", 10000)
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("max")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getMaxPriceProduct()).thenReturn(maxProduct)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with max price: </h1> ${maxProduct.name} ${maxProduct.price}\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testMaxNull() {
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("max")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getMaxPriceProduct()).thenReturn(null)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with max price: </h1> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testCount() {
        val cnt = 19
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("count")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getCount()).thenReturn(cnt)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "   Number of products: $cnt\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testSum() {
        val sum = 122334L
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn("sum")
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getSumPrices()).thenReturn(sum)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "   Summary price: $sum\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }

    @Test
    fun testUnknownCommand() {
        val command = "abacababab"
        val request = mock(HttpServletRequest::class.java)
        `when`(request.getParameter("command")).thenReturn(command)
        val dao = mock(ProductReadDao::class.java)
        `when`(dao.getSumPrices()).thenReturn(42)
        val servlet = QueryServlet(dao)
        val result = Jsoup.parse(servlet.processRequest(request)).toString()
        val expectedResult =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "  Unknown command: $command\n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedResult)
    }
}