package cz.bestak.deepresearch.feature.llm.domain

data class ToolCall(
    val toolCallId: String,
    val name: String,
    val arguments: Map<String, String>
)