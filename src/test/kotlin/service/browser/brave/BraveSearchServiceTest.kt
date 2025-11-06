package cz.bestak.deepresearch.domain.parser.service.browser.brave

import cz.bestak.deepresearch.domain.parser.common.FakeHttpClient
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class BraveSearchServiceTest {

    @Test
    fun `search returns parsed results when API responds correctly`() = runBlocking {
        val jsonResponse = """
            {
                "web": {
                    "results": [
                        {
                            "title": "Title 1",
                            "description": "Description 1",
                            "url": "https://example.com/1",
                            "extra_snippets": ["Extra 1"],
                            "cluster": [
                                {"title": "Cluster 1", "url": "https://cluster.com/1"}
                            ]
                        }
                    ]
                }
            }
        """.trimIndent()

        val service = BraveSearchService(FakeHttpClient(jsonResponse))
        val results = service.search("query", 1)

        assertEquals(1, results.size)
        assertEquals("Title 1", results[0].title)
        assertEquals(1, results[0].clusters?.size)
    }

    @Test
    fun `search returns empty list when API throws exception`() = runBlocking {
        val service = BraveSearchService(FakeHttpClient(response = null, shouldThrow = true))
        val results = service.search("query", 1)
        assertEquals(0, results.size)
    }
}