import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

fun ApplicationCall.getString(name: String): String? = request.queryParameters[name]
fun ApplicationCall.getLong(name: String): Long? = getString(name)?.toLong()
fun ApplicationCall.getCompany(): String? = getString("company")
fun ApplicationCall.getCount(): Long? = getLong("count")
fun ApplicationCall.getPrice(): Long? = getLong("price")
fun ApplicationCall.getId(): Long? = getLong("id")
fun ApplicationCall.getName(): String? = getString("name")

suspend fun ApplicationCall.respondNotEnoughParams(): Unit =
    respondText("Not all params are provided", status = HttpStatusCode.BadRequest)

suspend fun ApplicationCall.respondError(e: Exception): Unit =
    respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
