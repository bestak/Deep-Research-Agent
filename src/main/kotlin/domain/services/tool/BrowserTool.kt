package cz.bestak.deepresearch.domain.services.tool

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class BrowserTool: Tool {
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

    override fun execute(arguments: Map<String, String>): String {
        print("Finding in browser: $arguments")
        return "No results found"
    }
}