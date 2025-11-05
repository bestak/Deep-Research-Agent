package cz.bestak.deepresearch.service.llm.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.chat.ToolType
import com.aallam.openai.api.chat.function
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.api.chat.Tool as OAITool
import com.aallam.openai.api.chat.ToolCall as OAIToolCall
import cz.bestak.deepresearch.domain.model.LLMResponse
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.Role
import cz.bestak.deepresearch.domain.model.ToolCall
import cz.bestak.deepresearch.domain.services.Tool
import cz.bestak.deepresearch.service.llm.LLMService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenAiLLMService(
    private val openAI: OpenAI,
    private val modelId: ModelId
): LLMService {

    override suspend fun complete(messages: List<Message>, tools: List<Tool>): LLMResponse {
        val chatCompletionRequest = ChatCompletionRequest(
            model = modelId,
            messages = messages.map { it.toChatMessage() },
            tools = tools.map { it.toOAITool() },
            toolChoice = ToolChoice.Auto
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val message = completion.choices.first().message

        val toolCalls = message.toolCalls?.mapNotNull { tc ->
            val function = tc as? OAIToolCall.Function
            function?.let { functionCall ->
                ToolCall(
                    name = functionCall.function.name,
                    arguments = parseToolArguments(tc.function.arguments)
                )
            }
        }

        return LLMResponse(
            content = completion.choices.first().message.content.orEmpty(),
            toolCalls = toolCalls
        )
    }

    private fun Message.toChatMessage(): ChatMessage {
        return ChatMessage(
            role = when (role) {
                Role.System -> ChatRole.System
                Role.User -> ChatRole.User
                Role.Tool -> ChatRole.Tool
            },
            content = content
        )
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
}