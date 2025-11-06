package cz.bestak.deepresearch.app

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.ResearchPlan
import cz.bestak.deepresearch.domain.services.InitialPlanParser
import cz.bestak.deepresearch.service.llm.LLMService

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