package cz.bestak.deepresearch.feature.tool.executor.specific

import cz.bestak.deepresearch.feature.tool.BrowserTool
import cz.bestak.deepresearch.feature.tool.connectors.browser.FakeBrowserSearchService
import cz.bestak.deepresearch.feature.tool.connectors.browser.SearchResult
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowserToolExecutorTest {

    @Test
    fun `should return error if query is missing`() = runBlocking {
        val fakeService = FakeBrowserSearchService()
        val executor = BrowserToolExecutor(fakeService)

        val result = executor.execute(emptyMap())

        assertEquals("No `query` argument passed for the tool `${BrowserTool.NAME}`.", result)
    }

    @Test
    fun `should return error if query is blank`() = runBlocking {
        val fakeService = FakeBrowserSearchService()
        val executor = BrowserToolExecutor(fakeService)

        val result = executor.execute(mapOf("query" to " "))

        assertEquals("Argument `query` of the tool `${BrowserTool.NAME}` is empty.", result)
    }

    @Test
    fun `should call search service with query and return JSON`() = runBlocking {
        val fakeService = FakeBrowserSearchService()
        fakeService.resultsToReturn = listOf(SEARCH_RESULT, SEARCH_RESULT)

        val executor = BrowserToolExecutor(fakeService)
        val query = "Query"

        val result = executor.execute(mapOf("query" to query))

        assertEquals(listOf(query), fakeService.queries)

        val expectedJson = Json.encodeToString(fakeService.resultsToReturn)
        assertEquals(expectedJson, result)
    }

    companion object {
        private val SEARCH_RESULT = SearchResult(
            "Title 1",
            "https://example.com/1",
            url = "https://example.com/1",
            extraSnippets = listOf("1", "2"),
            clusters = listOf(
                SearchResult.Cluster(title = "Cluster 1", url = "https://example.com/1")
            )
        )
    }
}