package clients

import org.junit.Test
import com.xebialabs.restito.server.StubServer
import org.junit.Assert.*
import com.xebialabs.restito.semantics.Condition.startsWithUri
import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action.stringContent
import com.xebialabs.restito.semantics.Condition.method
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.glassfish.grizzly.http.Method
import java.io.Closeable

class KtorAsyncHttpClientTester {
    @Test
    fun testGetRequests() = runBlocking {
        val client = KtorAsyncHttpClient()
        val port = 2517
        withStubServer(port) {
            whenHttp(it)
                .match(method(Method.GET), startsWithUri("/ping"))
                .then(stringContent("pong"))

            val response = client.get("http://localhost:$port/ping")
            assertEquals(response, "pong")
        }
    }

    private suspend fun withStubServer(port: Int, callback: suspend CoroutineScope.(StubServer) -> Unit) {
        object : Closeable {
            val server = StubServer(port)

            override fun close() {
                server.stop()
            }
        }.use {
            it.server.start()
            GlobalScope.callback(it.server)
        }
    }
}