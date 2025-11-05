package cz.bestak.deepresearch.app

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.ResearchPlan
import cz.bestak.deepresearch.domain.model.Role
import cz.bestak.deepresearch.domain.parser.InitialPlanParser
import cz.bestak.deepresearch.service.llm.LLMService

class InitialPlanCreator(
    private val fastLLM: LLMService,
    private val instructionPrompt: String,
    private val initialPlanParser: InitialPlanParser
) {

    suspend fun create(userQuery: String): ResearchPlan {
        val result = fastLLM.complete(
            listOf(
                Message(Role.System, instructionPrompt),
                Message(Role.User, userQuery)
            )
        )
        return initialPlanParser.parse(result.content)
    }
}