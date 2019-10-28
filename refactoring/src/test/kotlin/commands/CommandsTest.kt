package commands

import command.countCommand
import command.maxPriceCommand
import command.minPriceCommand
import command.pricesSumCommand
import dao.ProductReadDao
import model.Product
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.jsoup.Jsoup

class CommandTest {
    @Test
    fun testMax() {
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getMaxPriceProduct()).thenReturn(Product("some good", 2517))
        val result = Jsoup.parse(maxPriceCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with max price: </h1> some good 2517\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMaxNull() {
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getMaxPriceProduct()).thenReturn(null)
        val result = Jsoup.parse(maxPriceCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with max price: </h1> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMin() {
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getMinPriceProduct()).thenReturn(Product("some good", 2517))
        val result = Jsoup.parse(minPriceCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with min price: </h1> some good 2517\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testMinNull() {
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getMinPriceProduct()).thenReturn(null)
        val result = Jsoup.parse(minPriceCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body> \n" +
                    "  <h1>Product with min price: </h1> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testSum() {
        val sum = 1337L
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getSumPrices()).thenReturn(sum)
        val result = Jsoup.parse(pricesSumCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "   Summary price: $sum\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }

    @Test
    fun testCount() {
        val cnt = 10
        val productsReadDao = mock(ProductReadDao::class.java)
        `when`(productsReadDao.getCount()).thenReturn(cnt)
        val result = Jsoup.parse(countCommand(productsReadDao)).toString()
        val expectedAnswer =
            "<html>\n" +
                    " <head></head>\n" +
                    " <body>\n" +
                    "   Number of products: $cnt\n" +
                    "  <br> \n" +
                    " </body>\n" +
                    "</html>"
        assertEquals(result, expectedAnswer)
    }
}
