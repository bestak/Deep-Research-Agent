package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService

interface ResearchAgent {
    suspend fun run(llm: LLMService, messages: List<Message>, maxSteps: Int = 10): String
}