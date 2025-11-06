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
        val result = llm.complete(
            listOf(
                Message.System(AgentInstructions.preProcessUserPrompt),
                Message.User(userQuery)
            )
        )
        val parsedPlan = try {
            initialPlanParser.parse(result.content)
        } catch (e: Exception) {
            System.err.println(result.content)
            throw e
        }

        println("[Plan] Plan created:")
        parsedPlan.steps.forEachIndexed { index, step ->
            println("Step #${index + 1}: ${step.title}")
        }
        return parsedPlan
    }
}