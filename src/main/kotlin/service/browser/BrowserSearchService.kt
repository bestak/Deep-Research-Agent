package cz.bestak.deepresearch.service.browser

interface BrowserSearchService {

    suspend fun search(query: String, maxResults: Int = 10): List<SearchResult>
}