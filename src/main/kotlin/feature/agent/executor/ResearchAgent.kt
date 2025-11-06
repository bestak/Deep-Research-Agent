package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.Tool
import cz.bestak.deepresearch.feature.llm.service.LLMService
import cz.bestak.deepresearch.feature.tool.ToolRegistry

class ResearchAgent(
    private val llm: LLMService,
    private val tools: List<Tool>,
    private val registry: ToolRegistry
) {

    suspend fun run(messages: List<Message>, maxSteps: Int = 10): String {
        val currentMessages = messages.toMutableList()
        repeat(maxSteps) {
            println("[Agent] Thinking...")
            val message = llm.complete(currentMessages, tools)
            currentMessages += message

            println("[Agent] ${message.content}")

            if (message is Message.Assistant) {
                message.toolCalls?.let { calls ->
                    calls.forEach { call ->
                        val tool = tools.find { it.name == call.name } ?: return@forEach
                        println("[Agent] Accessing tool ${tool.name}")

                        val executor = registry.findByName(tool.name)
                        executor.execute(call.arguments).let { toolRes ->
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
        const val STEP_INVALID = "Research step wasn't completed successfully, call limit reached."
        const val END_STEP_TAG = "<END_OF_STEP>"
    }
}