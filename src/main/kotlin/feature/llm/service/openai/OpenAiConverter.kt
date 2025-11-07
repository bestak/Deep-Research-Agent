package cz.bestak.deepresearch.feature.llm.service.openai

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.ToolId
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.domain.ToolCall
import cz.bestak.deepresearch.feature.tool.Tool
import com.aallam.openai.api.chat.Tool as OAITool
import com.aallam.openai.api.chat.ToolCall as OAIToolCall
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.mapValues

class OpenAiConverter {

    fun toChatMessage(message: Message): ChatMessage {
        return ChatMessage(
            role = when (message) {
                is Message.System -> ChatRole.System
                is Message.User -> ChatRole.User
                is Message.Tool -> ChatRole.Tool
                is Message.Assistant -> ChatRole.Assistant
            },
            content = message.content,
            toolCalls = (message as? Message.Assistant)?.toolCalls?.map { it.toOAIToolCall() },
            toolCallId = (message as? Message.Tool)?.toolCallId?.let { ToolId(it) }
        )
    }

    fun toMessage(chatMessage: ChatMessage): Message {
        val content = chatMessage.content.orEmpty()
        return when (chatMessage.role) {
            ChatRole.System -> Message.System(content)
            ChatRole.User -> Message.User(content)
            ChatRole.Assistant -> {
                val toolCalls = chatMessage.toolCalls?.mapNotNull { it.toToolCall() }
                Message.Assistant(content, toolCalls)
            }

            ChatRole.Tool -> Message.Tool(content, chatMessage.toolCallId?.id.orEmpty())
            else -> Message.System(content)
        }
    }

    fun toOAITool(tool: Tool): OAITool {
        return OAITool(
            type = ToolType(tool.type.value),
            function = FunctionTool(
                name = tool.name,
                description = tool.description,
                parameters = Parameters(tool.getParameters())
            )
        )
    }

    private fun OAIToolCall.toToolCall(): ToolCall? {
        val function = this as? OAIToolCall.Function
        return function?.let { functionCall ->
            ToolCall(
                toolCallId = functionCall.id.id,
                name = functionCall.function.name,
                arguments = parseToolArguments(this.function.arguments)
            )
        }
    }

    private fun ToolCall.toOAIToolCall(): OAIToolCall {
        return OAIToolCall.Function(
            id = ToolId(toolCallId),
            function = FunctionCall(
                nameOrNull = name,
                argumentsOrNull = Json.encodeToString(arguments)
            )
        )
    }

    private fun parseToolArguments(jsonString: String): Map<String, String> {
        return try {
            val jsonElement = Json.parseToJsonElement(jsonString)
            if (jsonElement is JsonObject) {
                jsonElement.mapValues { (_, v) -> v.jsonPrimitive.content }
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            // invalid JSON or unexpected structure
            emptyMap()
        }
    }
}