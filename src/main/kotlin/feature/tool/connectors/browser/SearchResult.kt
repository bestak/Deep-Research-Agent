package cz.bestak.deepresearch.feature.tool.connectors.browser

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val title: String,
    val description: String,
    val url: String,
    val extraSnippets: List<String>? = null,
    val clusters: List<Cluster>? = null
) {

    @Serializable
    data class Cluster(
        val title: String,
        val url: String
    )
}