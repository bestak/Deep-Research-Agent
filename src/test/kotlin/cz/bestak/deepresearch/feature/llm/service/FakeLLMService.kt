package cz.bestak.deepresearch.feature.llm.service

import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.Tool

class FakeLLMService(
    private val response: String = "Default fake response"
) : LLMService {
    override suspend fun complete(messages: List<Message>, tools: List<Tool>): Message {
        return Message.Assistant(response)
    }
}