package cz.bestak.deepresearch.feature.llm.service

import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.Tool

interface LLMService {

    suspend fun complete(messages: List<Message>, tools: List<Tool> = emptyList()): Message
}