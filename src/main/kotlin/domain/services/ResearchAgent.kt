package cz.bestak.deepresearch.domain.services

import cz.bestak.deepresearch.service.llm.LLMService

class ResearchAgent(
    private val llm: LLMService,
    private val tools: Map<String, Tool>
) {

    fun run() {

    }

}