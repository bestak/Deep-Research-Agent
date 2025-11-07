package cz.bestak.deepresearch.feature.llm.service

import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentImpl
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.domain.ToolCall
import cz.bestak.deepresearch.feature.tool.Tool

class FakeLLMService(
    private val messagesToToolCalls: Map<Int, List<ToolCall>> = emptyMap(),
    private val stepContent: Map<Int, String> = emptyMap()
) : LLMService {

    var callCount = 0
    val receivedMessages = mutableListOf<List<Message>>()

    override suspend fun complete(messages: List<Message>, tools: List<Tool>): Message {
        callCount++
        receivedMessages += messages.toList()

        val calls = messagesToToolCalls[callCount] ?: emptyList()
        val content = stepContent[callCount] ?: "Step $callCount content ${ResearchAgentImpl.END_STEP_TAG}"
        return Message.Assistant(content, toolCalls = calls)
    }
}