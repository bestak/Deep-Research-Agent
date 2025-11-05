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
import cz.bestak.deepresearch.domain.model.LLMResponse
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.Role
import cz.bestak.deepresearch.domain.services.Tool
import cz.bestak.deepresearch.service.llm.LLMService

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
        return LLMResponse(
            content = completion.choices.first().message.content.orEmpty()
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
}