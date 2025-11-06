package cz.bestak.deepresearch.app

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.ResearchPlan
import cz.bestak.deepresearch.domain.prompts.AgentInstructions
import cz.bestak.deepresearch.domain.services.ResearchAgent
import cz.bestak.deepresearch.domain.services.tool.BrowserTool
import cz.bestak.deepresearch.domain.services.tool.PageLoaderTool
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import cz.bestak.deepresearch.service.http.KtorHttpClient
import cz.bestak.deepresearch.service.llm.LLMService
import cz.bestak.deepresearch.service.pageloader.HttpWebPageLoaderService

class ResearchAgentService(
    private val agentLLM: LLMService
) {

    suspend fun executePlan(plan: ResearchPlan): String {
        val client = KtorHttpClient()
        val browserSearchService = BraveSearchService(client)
        val webPageLoaderService = HttpWebPageLoaderService(client)

        val tools = listOf(
            BrowserTool(browserSearchService),
            PageLoaderTool(webPageLoaderService)
        )

        val researchAgent = ResearchAgent(agentLLM, tools)

        val allMessages = mutableListOf<Message>(
            Message.System(AgentInstructions.getDeepResearchSystemPrompt(MAX_STEP_COUNT))
        )
        plan.steps.forEachIndexed { index, step ->
            println("[Steps] Starting step ${index + 1}/${plan.steps.size} - ${step.title}")

            allMessages += Message.User("Execute this step (${index + 1} / ${plan.steps.size}): ${step.title}: ${step.description}")
            val stepResult = researchAgent.run(allMessages, maxSteps = MAX_STEP_COUNT)
            allMessages += Message.Assistant(stepResult)

            println("[Steps] Finished step ${index + 1}/${plan.steps.size}")
        }

        println("[Steps] Finished all steps, summarizing results.")
        allMessages += Message.User(AgentInstructions.summarizeResult)
        val summarizedResult = researchAgent.run(allMessages)
        return summarizedResult
    }


    companion object {
        const val MAX_STEP_COUNT = 10
    }
}
