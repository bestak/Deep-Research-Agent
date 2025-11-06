package cz.bestak.deepresearch.feature.tool

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class BrowserTool: Tool {
    override val type: ToolType = ToolType.Function
    override val name: String = NAME
    override val description: String = """
        Performs real-time searches on the web to retrieve relevant information, news, or data. 
        Returns json representation of 10 results results along with url links that can be used later."
    """.trimIndent()

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

    companion object {
        const val NAME = "browser"
    }
}