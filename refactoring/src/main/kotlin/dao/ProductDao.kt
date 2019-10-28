package dao

import model.Product

interface ProductReadDao {
    fun getAllProducts(): List<Product>

    fun getMinPriceProduct(): Product?

    fun getMaxPriceProduct(): Product?

    fun getCount(): Int

    fun getSumPrices(): Long
}

interface ProductWriteDao {
    fun createProductsTable()

    fun addProduct(product: Product)
}