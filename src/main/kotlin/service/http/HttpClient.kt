package cz.bestak.deepresearch.service.http

interface HttpClient {
    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
    ): String
}