package cz.bestak.deepresearch.feature.tool.connectors.pageloader

import cz.bestak.deepresearch.common.FakeHttpClient
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpWebPageLoaderServiceTest {
    @Test
    fun `load page and return it`() = runBlocking {
        val pageContent = "some page content"
        val service = HttpWebPageLoaderService(FakeHttpClient(pageContent))

        val result = service.load("https://www.test.com")

        assertEquals(pageContent, result)
    }

    @Test
    fun `load returns empty string when API throws exception`() = runBlocking {
        val pageContent = "some page content"
        val service = HttpWebPageLoaderService(FakeHttpClient(pageContent, shouldThrow = true))

        val result = service.load("https://www.test.com")

        assertEquals(null, result)
    }

}