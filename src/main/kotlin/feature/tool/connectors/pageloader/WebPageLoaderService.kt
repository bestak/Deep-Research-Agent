package cz.bestak.deepresearch.feature.tool.connectors.pageloader

interface WebPageLoaderService {

    suspend fun load(url: String): String?
}