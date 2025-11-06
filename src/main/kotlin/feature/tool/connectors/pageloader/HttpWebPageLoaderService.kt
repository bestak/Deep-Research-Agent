package cz.bestak.deepresearch.feature.tool.connectors.pageloader

import cz.bestak.deepresearch.service.http.HttpClient

class HttpWebPageLoaderService(
    private val client: HttpClient
): WebPageLoaderService {

    override suspend fun load(url: String): String {
        return client.get(url)
    }
}