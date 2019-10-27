import clients.KtorAsyncHttpClient
import clients.VKClient
import com.typesafe.config.ConfigFactory
import configs.VKConfig
import kotlinx.coroutines.runBlocking
import time.getSearchTimestamps
import java.io.File
import java.util.*
import java.util.regex.Pattern

fun main(args: Array<String>) = runBlocking {
    require(args.size == 2 && Pattern.compile("[1-9][0-9]*").matcher(args[1]).matches()) {
        "Usage: <hashtag> <number of hours>"
    }
    val hastTag = args[0]
    val hours = args[1].toInt()
    require(hours in (1..24)) {
        "Number of hours must be between 1 and 24"
    }
    val parsedFile = ConfigFactory.parseFile(File("src/main/resources/application.conf"))
    val config = VKConfig(parsedFile.getConfig("vk"))
    val curDate = Date()
    val searchTimestamps = getSearchTimestamps(curDate, hours)
    val client = VKClient(KtorAsyncHttpClient(), config)
    val res = searchTimestamps.map { (startTime, endTime) -> client.getResponse(hastTag, startTime, endTime) }
        .map { it?.response?.totalCount?.toString() ?: "???" }
    for (i in (0 until hours)) {
        println("В промежуток с ${hours - i} по ${hours - i - 1} часов назад было ${res[i]} новостей с хэштегом #$hastTag")
    }
}