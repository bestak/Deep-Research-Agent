package cz.bestak.deepresearch.feature.tool.executor.specific

import cz.bestak.deepresearch.feature.tool.connectors.pageloader.FakePageLoaderService
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class PageLoaderToolExecutorTest {

    @Test
    fun `should return error if url is missing`() = runBlocking {
        val fakeService = FakePageLoaderService()
        val executor = PageLoaderToolExecutor(fakeService)

        val result = executor.execute(emptyMap())

        assertEquals(PageLoaderToolExecutor.MISSING_ARGUMENT, result)
    }

    @Test
    fun `should return error if url is blank`() = runBlocking {
        val fakeService = FakePageLoaderService()
        val executor = PageLoaderToolExecutor(fakeService)

        val result = executor.execute(mapOf("url" to " "))

        assertEquals(PageLoaderToolExecutor.ARGUMENT_EMPTY, result)
    }

    @Test
    fun `should return no content if content is empty`() = runBlocking {
        val fakeService = FakePageLoaderService()
        fakeService.resultToReturn = null
        val executor = PageLoaderToolExecutor(fakeService)

        val result = executor.execute(mapOf("url" to "https://example.com"))

        assertEquals(PageLoaderToolExecutor.NO_CONTENT, result)
    }

    @Test
    fun `should load web page using url and return JSON`() = runBlocking {
        val fakeService = FakePageLoaderService()
        fakeService.resultToReturn = "page contents"

        val executor = PageLoaderToolExecutor(fakeService)
        val url = "https://example.com"

        val result = executor.execute(mapOf("url" to url))

        assertEquals(listOf(url), fakeService.urls)
        assertEquals(fakeService.resultToReturn, result)
    }
}