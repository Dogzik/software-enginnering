package config

import java.nio.file.Files
import java.nio.file.Path

interface SQLCommands {
    val createDB: String

    val insertProduct: String

    val getAllProducts: String

    val getMinPriceProduct: String

    val getMaxPriceProduct: String

    val getCountProducts: String

    val getSumPrices: String
}

fun readFileToString(path: Path) = Files.newBufferedReader(path).useLines { it.joinToString(separator = "\n") }

class SQLCommandsImpl(SQLFolder: Path) : SQLCommands {
    override val createDB: String = readFileToString(SQLFolder.resolve("create-db.sql"))

    override val insertProduct: String = readFileToString(SQLFolder.resolve("insert-product.sql"))

    override val getAllProducts: String = readFileToString(SQLFolder.resolve("get-all-products.sql"))

    override val getMinPriceProduct: String = readFileToString(SQLFolder.resolve("get-min-price-product.sql"))

    override val getMaxPriceProduct: String = readFileToString(SQLFolder.resolve("get-min-price-product.sql"))

    override val getCountProducts: String = readFileToString(SQLFolder.resolve("get-count-products.sql"))

    override val getSumPrices: String = readFileToString(SQLFolder.resolve("get-sum-prices.sql"))
}
