package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.agent.domain.ResearchStep
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService

class ResearchAgentService(
    private val researchAgent: ResearchAgent
) {

    suspend fun executePlan(llm: LLMService, plan: ResearchPlan): String {
        val allMessages = mutableListOf<Message>(
            Message.System(AgentInstructions.getDeepResearchSystemPrompt(MAX_STEP_COUNT))
        )
        plan.steps.forEachIndexed { index, step ->
            println("[Steps] Starting step ${index + 1}/${plan.steps.size} - ${step.title}")

            allMessages += Message.User(getStepIntroMessage(index, plan, step))
            val stepResult = researchAgent.run(llm, allMessages, maxSteps = MAX_STEP_COUNT)
            allMessages += Message.Assistant(stepResult.response)

            println("[Steps] Finished step ${index + 1}/${plan.steps.size}")
        }

        println("[Steps] Finished all steps, summarizing results.")
        allMessages += Message.User(AgentInstructions.summarizeResult)
        val summarizedResult = researchAgent.run(llm, allMessages, maxSteps = MAX_STEP_COUNT)
        return summarizedResult.response
    }

    fun getStepIntroMessage(index: Int, plan: ResearchPlan, step: ResearchStep): String {
        return "Execute this step (${index + 1} / ${plan.steps.size}): ${step.title}: ${step.description}"
    }


    companion object {
        const val MAX_STEP_COUNT = 15
    }
}
