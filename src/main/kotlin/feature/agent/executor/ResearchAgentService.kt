package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.BrowserTool
import cz.bestak.deepresearch.feature.tool.PageLoaderTool
import cz.bestak.deepresearch.feature.llm.service.LLMService
import cz.bestak.deepresearch.feature.tool.ToolRegistry

class ResearchAgentService(
    private val toolRegistry: ToolRegistry
) {

    suspend fun executePlan(llm: LLMService, plan: ResearchPlan): String {
        val tools = listOf(
            BrowserTool(),
            PageLoaderTool()
        )

        val researchAgent = ResearchAgent(llm, tools, toolRegistry)

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
