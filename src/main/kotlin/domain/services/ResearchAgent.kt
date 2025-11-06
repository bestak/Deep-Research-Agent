package cz.bestak.deepresearch.domain.services

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.services.tool.Tool
import cz.bestak.deepresearch.service.llm.LLMService

class ResearchAgent(
    private val llm: LLMService,
    private val tools: List<Tool>
) {

    suspend fun run(messages: List<Message>): String {
        val currentMessages = messages.toMutableList()
        repeat(MAX_STEP_COUNT) {
            println("[Agent] Thinking...")
            val message = llm.complete(currentMessages, tools)
            currentMessages += message

            println("[Agent] ${message.content}")

            if (message is Message.Assistant) {
                message.toolCalls?.let { calls ->
                    calls.forEach { call ->
                        val tool = tools.find { it.name == call.name }
                        println("[Agent] Accessing tool ${tool?.name}")

                        tool?.execute(call.arguments)?.let { toolRes ->
                            currentMessages += Message.Tool(toolRes, call.toolCallId)
                        }
                    }
                }
            }

            if (message.content.contains(END_STEP_TAG)) {
                println("[Agent] Ending step, found end tag")
                return message.content.replace(END_STEP_TAG, "")
            }
        }
        return STEP_INVALID
    }

    companion object {
        const val MAX_STEP_COUNT = 10
        const val STEP_INVALID = "Research step wasn't completed successfully, call limit reached."
        const val END_STEP_TAG = "<END_OF_STEP>"
    }
}