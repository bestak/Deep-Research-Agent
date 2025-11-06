package cz.bestak.deepresearch.feature.tool

import cz.bestak.deepresearch.feature.tool.connectors.pageloader.WebPageLoaderService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class PageLoaderTool: Tool {
    override val type: ToolType = ToolType.Function
    override val name: String = NAME
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

    companion object {
        const val NAME = "load_webpage"
    }
}