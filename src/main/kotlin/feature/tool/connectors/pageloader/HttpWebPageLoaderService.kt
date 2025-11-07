package cz.bestak.deepresearch.feature.tool.connectors.pageloader

import cz.bestak.deepresearch.service.http.HttpClient
import org.jsoup.Jsoup

class HttpWebPageLoaderService(
    private val client: HttpClient
): WebPageLoaderService {

    override suspend fun load(url: String): String? {
        return try {
            val content = client.get(url)
            val parsed = Jsoup.parse(content)
            return parsed.text()
        } catch (_: Exception) {
            null
        }
    }
}