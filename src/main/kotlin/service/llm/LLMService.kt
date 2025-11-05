package cz.bestak.deepresearch.service.llm

import cz.bestak.deepresearch.domain.model.LLMResponse
import cz.bestak.deepresearch.domain.model.Message

interface LLMService {

    suspend fun complete(messages: List<Message>): LLMResponse


}