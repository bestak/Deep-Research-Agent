package cz.bestak.deepresearch.domain.model

data class LLMResponse(
    val content: String,
    val toolCalls: List<ToolCall>? = null,
)