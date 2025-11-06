package cz.bestak.deepresearch.feature.agent.planner

import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService

class InitialPlanService(
    private val fastLLM: LLMService,
    private val instructionPrompt: String,
    private val initialPlanParser: InitialPlanParser
) {

    suspend fun create(userQuery: String): ResearchPlan {
        println("[Plan] Creating...")
        val result = fastLLM.complete(
            listOf(
                Message.System(instructionPrompt),
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