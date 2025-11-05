package cz.bestak.deepresearch.service.llm.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.domain.model.LLMResponse
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.Role
import cz.bestak.deepresearch.service.llm.LLMService

class OpenAiLLMService(
    private val openAI: OpenAI
): LLMService {

    override suspend fun complete(messages: List<Message>): LLMResponse {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages.map { it.toChatMessage() },
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
}