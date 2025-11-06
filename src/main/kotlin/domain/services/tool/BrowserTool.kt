package cz.bestak.deepresearch.domain.services.tool

import cz.bestak.deepresearch.service.browser.BrowserSearchService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class BrowserTool(
    private val browserSearchService: BrowserSearchService
): Tool {
    override val type: ToolType = ToolType.Function
    override val name: String = "browser"
    override val description: String = ""

    override fun getParameters(): JsonElement {
        return buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {
                putJsonObject("query") {
                    put("type", "string")
                    put("description", "The search query to look up in the browser")
                }
            }
            putJsonArray("required") {
                add("query")
            }
        }
    }

    override suspend fun execute(arguments: Map<String, String>): String {
        val query = arguments.getOrElse("query") {
            return "No `query` argument passed for the tool `$name`."
        }
        if (query.isBlank()) {
            return "Argument `query` of the tool `$name` is empty."
        }
        val browserResults = browserSearchService.search(query)
        return Json.encodeToString(browserResults)
    }
}