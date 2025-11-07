package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.LLMService

class FakeResearchAgent : ResearchAgent {
    data class RunCall(val llm: LLMService, val messages: List<Message>, val maxSteps: Int?)

    val runCalls = mutableListOf<RunCall>()

    override suspend fun run(llm: LLMService, messages: List<Message>, maxSteps: Int): ResearchAgent.Result {
        runCalls += RunCall(llm, messages, maxSteps)

        val lastUserMessage = messages.lastOrNull()?.content.orEmpty()
        return if (lastUserMessage.contains(AgentInstructions.summarizeResult.take(10))) {
            ResearchAgent.Result(getSummary(runCalls.size - 1))
        } else {
            ResearchAgent.Result(STEP_RESULT)
        }
    }

    companion object {
        const val STEP_RESULT = "FAKE_STEP_RESULT"

        fun getSummary(doneSteps: Int): String = "SUMMARY: Completed $doneSteps research steps."
    }
}