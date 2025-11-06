package cz.bestak.deepresearch.domain.model

sealed interface Message {
    val content: String

    data class Assistant(override val content: String, val toolCalls: List<ToolCall>?): Message

    data class System(override val content: String): Message

    data class User(override val content: String): Message

    data class Tool(
        override val content: String,
        val toolCallId: String
    ): Message
}
