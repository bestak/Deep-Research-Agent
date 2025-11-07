package cz.bestak.deepresearch.feature.agent

import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentService
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanService
import cz.bestak.deepresearch.feature.llm.service.LLMModelProvider

class DeepResearchAgent(
    private val initialPlanService: InitialPlanService,
    private val researchAgentService: ResearchAgentService,
    private val modelProvider: LLMModelProvider
) {

    suspend fun run(query: String, fastModel: String, agentModel: String): String {
        val fastLLM = modelProvider.getModel("gpt-3.5-turbo")
        val plan = initialPlanService.create(fastLLM, query)

        val agentLLM = modelProvider.getModel("gpt-5-mini-2025-08-07")
        val result = researchAgentService.executePlan(agentLLM, plan)

        println("=".repeat(80))
        println(result)
        return result
    }
}
