package cz.bestak.deepresearch.service.http

import io.ktor.client.HttpClient as KtorClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class KtorHttpClient(
    val client: KtorClient = KtorClient(CIO)
): HttpClient {
    override suspend fun get(
        url: String,
        headers: Map<String, String>,
        params: Map<String, String>
    ): String {
        val response: HttpResponse = client.get(url) {
            headers.forEach { (k, v) -> header(k, v) }
            params.forEach { (k, v) -> parameter(k, v) }
        }
        val responseBody = response.bodyAsText()
        return responseBody
    }
}