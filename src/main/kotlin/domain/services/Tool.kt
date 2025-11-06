package cz.bestak.deepresearch.domain.services

import kotlinx.serialization.json.JsonElement

interface Tool {

    val type: ToolType

    val name: String
    val description: String

    fun getParameters(): JsonElement

    fun execute(arguments: Map<String, String>): String

}

class ToolType(val value: String) {
    companion object {
        val Function: ToolType = ToolType("function")
    }
}
