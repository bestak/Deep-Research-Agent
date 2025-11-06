package cz.bestak.deepresearch.service.browser.brave

import cz.bestak.deepresearch.feature.tool.connectors.browser.BrowserSearchService
import cz.bestak.deepresearch.feature.tool.connectors.browser.SearchResult
import cz.bestak.deepresearch.service.http.HttpClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.json.*

class BraveSearchService(
    private val httpClient: HttpClient
): BrowserSearchService {

    override suspend fun search(query: String, maxResults: Int): List<SearchResult> {
        val output = try {
            val responseBody = httpClient.get(
                url ="https://api.search.brave.com/res/v1/web/search",
                headers = mapOf(
                    "X-Subscription-Token" to dotenv()["BRAVE_API_KEY"]
                ),
                params = mapOf(
                    "q" to query,
                    "count" to maxResults.toString(),
                    "country" to "us",
                    "search_lang" to "en"
                )
            )
            val json = Json.parseToJsonElement(responseBody).jsonObject
            parseResponse(json)
        } catch (e: Exception) {
            System.err.print("Cannot search brave engine: ${e.message}")
            null
        }
        return output.orEmpty()
    }

    private fun parseResponse(json: JsonObject): List<SearchResult> {
        val webResults = json["web"]?.jsonObject?.get("results")?.jsonArray
        return webResults?.map { webResult ->
            val rawRes = webResult.jsonObject
            SearchResult(
                title = rawRes["title"].toStringOrEmpty(),
                description = rawRes["description"].toStringOrEmpty(),
                url = rawRes["url"].toStringOrEmpty(),
                extraSnippets = rawRes["extra_snippets"]?.jsonArray?.map {
                    it.toStringOrEmpty()
                },
                clusters = rawRes["cluster"]?.jsonArray?.map {
                    val rawCluster = it.jsonObject
                    SearchResult.Cluster(
                        title = rawCluster["title"].toStringOrEmpty(),
                        url = rawCluster["url"].toStringOrEmpty(),
                    )
                }
            )
        }.orEmpty()
    }

    private fun JsonElement?.toStringOrEmpty(): String {
        return this?.jsonPrimitive?.content.orEmpty()
    }
}