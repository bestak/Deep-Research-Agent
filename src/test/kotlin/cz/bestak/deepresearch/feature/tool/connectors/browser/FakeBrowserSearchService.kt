package cz.bestak.deepresearch.feature.tool.connectors.browser

class FakeBrowserSearchService : BrowserSearchService {
    val queries = mutableListOf<String>()
    var resultsToReturn: List<SearchResult> = emptyList()

    override suspend fun search(
        query: String,
        maxResults: Int
    ): List<SearchResult> {
        queries += query
        return resultsToReturn
    }
}