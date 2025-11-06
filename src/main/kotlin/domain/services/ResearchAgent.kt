package cz.bestak.deepresearch.domain.services

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.Role
import cz.bestak.deepresearch.service.llm.LLMService

class ResearchAgent(
    private val llm: LLMService,
    private val tools: List<Tool>
) {

    suspend fun run(messages: List<Message>): String {
        val currentMessages = messages.toMutableList()
        repeat(REPEAT_COUNT) {
            print("Running agent step...")
            val response = llm.complete(currentMessages, tools)
            response.toolCalls?.let { calls ->
                calls.forEach { call ->
                    val tool = tools.find { it.name == call.name }
                    tool?.execute(call.arguments)?.let { toolRes ->
                        print("Executed tool with name: ${tool.name}, and got result: $toolRes")
                        currentMessages += Message(Role.Tool, toolRes)

                    }
                }
            } ?: throw Exception()// return response.content
        }
        return STEP_INVALID
    }

    companion object {
        const val REPEAT_COUNT = 6
        const val STEP_INVALID = "Research step wasn't completed successfully, call limit reached."
    }
}