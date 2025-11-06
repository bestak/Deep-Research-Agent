package cz.bestak.deepresearch.domain.services.tool

import cz.bestak.deepresearch.service.pageloader.WebPageLoaderService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class PageLoaderTool(
    private val webPageLoaderService: WebPageLoaderService
): Tool {
    override val type: ToolType = ToolType.Function
    override val name: String = "load_webpage"
    override val description: String = """
        Fetches the full content of a specified web page. 
        Can be used to extract text, analyze page content, or access structured data from websites."
    """.trimIndent()

    override fun getParameters(): JsonElement {
        return buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("url") {
                    put("type", "string")
                    put("description", "The url of the page to load")
                }
            }
            putJsonArray("required") {
                add("url")
            }
        }
    }

    override suspend fun execute(arguments: Map<String, String>): String {
        val url = arguments.getOrElse("url") {
            return "No `url` argument passed for the tool `$name`."
        }
        if (url.isBlank()) {
            return "Argument `url` of the tool `$name` is empty."
        }
        val browserResults = webPageLoaderService.load(url)
        return Json.encodeToString(browserResults)
    }
}