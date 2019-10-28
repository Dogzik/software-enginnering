package command

import dao.ProductReadDao
import model.Product
import response.ResponseBuilder

fun extremumPriceProductCommand(
    type: String,
    productsReadDao: ProductReadDao,
    query: ProductReadDao.() -> Product?
): String {
    val responseBuilder = ResponseBuilder("<h1>Product with $type price: </h1>")
    val maxPriceProduct = productsReadDao.query()
    maxPriceProduct?.let {
        responseBuilder.addResponseElement("${it.name}\t${it.price}")
    }
    return responseBuilder.buildAnswer()
}

fun <T> aggregateCommand(header: String, productsReadDao: ProductReadDao, query: ProductReadDao.() -> T): String {
    val responseBuilder = ResponseBuilder(header)
    val response = productsReadDao.query()
    responseBuilder.addResponseElement(response.toString())
    return responseBuilder.buildAnswer()
}

fun maxPriceCommand(productsReadDao: ProductReadDao) =
    extremumPriceProductCommand("max", productsReadDao, ProductReadDao::getMaxPriceProduct)

fun minPriceCommand(productsReadDao: ProductReadDao) =
    extremumPriceProductCommand("min", productsReadDao, ProductReadDao::getMinPriceProduct)

fun countCommand(productsReadDao: ProductReadDao) =
    aggregateCommand("Number of products: ", productsReadDao, ProductReadDao::getCount)

fun pricesSumCommand(productsReadDao: ProductReadDao) =
    aggregateCommand("Summary price: ", productsReadDao, ProductReadDao::getSumPrices)
