package cz.bestak.deepresearch.service.browser

data class SearchResult(
    val title: String,
    val description: String,
    val url: String,
    val extraSnippets: String? = null,
    val clusters: List<Cluster>? = null
) {
    data class Cluster(
        val title: String,
        val url: String
    )
}