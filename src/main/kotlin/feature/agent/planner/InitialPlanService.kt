package cz.bestak.deepresearch.feature.agent.planner

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService

class InitialPlanService(
    private val initialPlanParser: InitialPlanParser
) {

    suspend fun create(llm: LLMService, userQuery: String): ResearchPlan {
        println("[Plan] Creating...")
        var parsedPlan: ResearchPlan? = null
        var attempts = 0
        while (parsedPlan == null) {
            attempts++
            val result = llm.complete(
                listOf(
                    Message.System(AgentInstructions.preProcessUserPrompt),
                    Message.User(userQuery)
                )
            )
            try {
                parsedPlan = initialPlanParser.parse(result.content)
            } catch (e: Exception) {
                println("[Plan] Planning LLM produced invalid JSON. Trying again.")

                if (attempts >= MAX_ATTEMPTS) {
                    throw Exception("Planning LLM wasn't able to produce valid JSON plan after ${MAX_ATTEMPTS}. Aborting.")
                }
            }
        }

        println("[Plan] Plan created:")
        parsedPlan.steps.forEachIndexed { index, step ->
            println("Step #${index + 1}: ${step.title}")
        }
        return parsedPlan
    }

    companion object {
        const val MAX_ATTEMPTS = 3
    }
}