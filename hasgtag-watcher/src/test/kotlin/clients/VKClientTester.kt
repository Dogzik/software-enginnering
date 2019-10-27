package clients

import com.typesafe.config.ConfigFactory
import configs.VKConfig
import io.mockk.mockk
import org.junit.Test
import io.mockk.coEvery
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import models.Response
import models.VKResponse
import org.junit.Assert.assertEquals
import java.io.File

class VKClientTester {
    private fun getTestConfig(): VKConfig = VKConfig(
        ConfigFactory.parseFile(
            File("src/test/resources/test-application.conf")
        ).getConfig("vk")
    )

    private fun getExpectedQuery(hashTag: String, startTime: Long, endTime: Long, testConfig: VKConfig): String {
        return "${testConfig.schema}://${testConfig.host}:${testConfig.port}/method/newsfeed.search?" +
                "q=%23$hashTag&" +
                "v=${testConfig.version.major}.${testConfig.version.minor}&" +
                "access_token=${testConfig.accessToken}&" +
                "count=0&" +
                "start_time=$startTime&" +
                "end_time=$endTime"
    }

    @Test
    fun testCorrectResponse() = runBlocking {
        val mockClient = mockk<AsyncHttpClient>()
        val testConfig = getTestConfig()
        val hashTag = "cat"
        val startTime = 13371488L
        val endTime = 13372517L
        val expectedQuery = getExpectedQuery(hashTag, startTime, endTime, testConfig)
        val testCount = 7
        val testResponse = "{\"response\":{\"items\":[],\"count\":$testCount,\"total_count\":$testCount}}"
        coEvery { mockClient.get(expectedQuery) } returns testResponse
        val testClient = VKClient(mockClient, testConfig)
        assertEquals(VKResponse(Response(testCount)), testClient.getResponse(hashTag, startTime, endTime))
    }


    @Test
    fun testIncorrectResponse() = runBlocking {
        val mockClient = mockk<AsyncHttpClient>()
        val testConfig = getTestConfig()
        val hashTag = "dog"
        val startTime = 100000000L
        val endTime = 200000000L
        val expectedQuery = getExpectedQuery(hashTag, startTime, endTime, testConfig)
        val testResponse = "I am not a JSON"
        coEvery { mockClient.get(expectedQuery) } returns testResponse
        val testClient = VKClient(mockClient, testConfig)
        assertEquals(null, testClient.getResponse(hashTag, startTime, endTime))
    }

    @Test
    fun testTimeOut() = runBlocking {
        val mockClient = mockk<AsyncHttpClient>()
        val testConfig = getTestConfig()
        val hashTag = "pig"
        val startTime = 300000000L
        val endTime = 400000000L
        val expectedQuery = getExpectedQuery(hashTag, startTime, endTime, testConfig)
        val testResponse = "{\"response\":{\"items\":[],\"count\":10,\"total_count\":10}}"
        val sleepTime = testConfig.timeout.multipliedBy(2)
        coEvery { mockClient.get(expectedQuery) } coAnswers {
            delay(sleepTime.toMillis())
            testResponse
        }
        val testClient = VKClient(mockClient, testConfig)
        assertEquals(null, testClient.getResponse(hashTag, startTime, endTime))
    }
}