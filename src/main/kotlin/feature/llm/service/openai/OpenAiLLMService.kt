package cz.bestak.deepresearch.feature.llm.service.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.Tool as OAITool
import com.aallam.openai.api.chat.ToolCall as OAIToolCall
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.chat.ToolId
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.domain.ToolCall
import cz.bestak.deepresearch.feature.tool.Tool
import cz.bestak.deepresearch.feature.llm.service.LLMService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenAiLLMService(
    private val openAI: OpenAI,
    private val modelId: ModelId
): LLMService {

    override suspend fun complete(messages: List<Message>, tools: List<Tool>): Message {
        val chatCompletionRequest = ChatCompletionRequest(
            model = modelId,
            messages = messages.map { it.toChatMessage() },
            tools = tools.map { it.toOAITool() },
            toolChoice = ToolChoice.Auto
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val message = completion.choices.first().message

        return message.toMessage()
    }

    private fun Message.toChatMessage(): ChatMessage {
        return ChatMessage(
            role = when (this) {
                is Message.System -> ChatRole.System
                is Message.User -> ChatRole.User
                is Message.Tool -> ChatRole.Tool
                is Message.Assistant -> ChatRole.Assistant
            },
            content = content,
            toolCalls = (this as? Message.Assistant)?.toolCalls?.map { it.toOAIToolCall() },
            toolCallId = (this as? Message.Tool)?.toolCallId?.let { ToolId(it) }
        )
    }

    private fun ChatMessage.toMessage(): Message {
        val content = content.orEmpty()
        return when (role) {
            ChatRole.System -> Message.System(content)
            ChatRole.User -> Message.User(content)
            ChatRole.Assistant -> {
                val toolCalls = toolCalls?.mapNotNull { it.toToolCall() }
                Message.Assistant(content, toolCalls)
            }
            ChatRole.Tool -> Message.Tool(content, toolCallId?.id.orEmpty())
            else -> Message.System(content)
        }
    }

    private fun Tool.toOAITool(): OAITool {
        return OAITool(
            type = ToolType(type.value),
            function = FunctionTool(
                name = name,
                description = description,
                parameters = Parameters(getParameters())
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
}