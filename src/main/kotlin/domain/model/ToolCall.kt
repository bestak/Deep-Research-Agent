package cz.bestak.deepresearch.domain.model

data class ToolCall(
    val name: String,
    val arguments: Map<String, String>
)