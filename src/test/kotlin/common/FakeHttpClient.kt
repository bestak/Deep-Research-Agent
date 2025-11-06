package cz.bestak.deepresearch.domain.parser.common

import cz.bestak.deepresearch.service.http.HttpClient

class FakeHttpClient(private val response: String?, private val shouldThrow: Boolean = false) : HttpClient {
    override suspend fun get(
        url: String,
        headers: Map<String, String>,
        params: Map<String, String>
    ): String {
        if (shouldThrow) throw RuntimeException("API error")
        return response ?: ""
    }
}