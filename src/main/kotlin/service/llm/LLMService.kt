package cz.bestak.deepresearch.service.llm

import cz.bestak.deepresearch.domain.model.LLMResponse
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.services.Tool

interface LLMService {

    suspend fun complete(messages: List<Message>, tools: List<Tool> = emptyList()): LLMResponse
}