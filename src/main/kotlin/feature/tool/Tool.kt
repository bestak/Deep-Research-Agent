package cz.bestak.deepresearch.feature.tool

import kotlinx.serialization.json.JsonElement

interface Tool {

    val type: ToolType

    val name: String
    val description: String

    fun getParameters(): JsonElement

    suspend fun execute(arguments: Map<String, String>): String

}

class ToolType(val value: String) {
    companion object {
        val Function: ToolType = ToolType("function")
    }
}
