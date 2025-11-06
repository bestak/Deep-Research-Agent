package cz.bestak.deepresearch.feature.tool.connectors.browser

interface BrowserSearchService {

    suspend fun search(query: String, maxResults: Int = 10): List<SearchResult>
}