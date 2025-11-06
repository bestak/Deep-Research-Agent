package cz.bestak.deepresearch.domain.model

data class LLMResponse(
    val messages: List<Message>,
    val toolCalls: List<ToolCall>? = null,
)