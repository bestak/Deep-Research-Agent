package cz.bestak.deepresearch.feature.llm.service.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService
import cz.bestak.deepresearch.feature.tool.Tool

class OpenAiLLMService(
    private val openAI: OpenAI,
    private val modelId: ModelId,
    private val openAiConverter: OpenAiConverter
): LLMService {

    override suspend fun complete(messages: List<Message>, tools: List<Tool>): Message {
        val chatCompletionRequest = ChatCompletionRequest(
            model = modelId,
            messages = messages.map { openAiConverter.toChatMessage(it) },
            tools = tools.map { openAiConverter.toOAITool(it) },
            toolChoice = ToolChoice.Auto
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val chatMessage = completion.choices.first().message

        return openAiConverter.toMessage(chatMessage)
    }
}