package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.Tool
import cz.bestak.deepresearch.feature.llm.service.LLMService
import cz.bestak.deepresearch.feature.tool.PageLoaderTool
import cz.bestak.deepresearch.feature.tool.ToolRegistry

class ResearchAgent(
    private val llm: LLMService,
    private val tools: List<Tool>,
    private val registry: ToolRegistry
) {

    suspend fun run(messages: List<Message>, maxSteps: Int = 10): String {
        val currentMessages = messages.toMutableList()
        var nmbOfPageLookups = 0

        (0..maxSteps).forEach { step ->
            println("[Agent] Thinking...")
            currentMessages += Message.User("Execute the current research step. Reasoning/tool use attempts remaining: ${maxSteps - step}")
            val message = llm.complete(currentMessages, tools)
            currentMessages += message

            println("[Agent] ${message.content}")

            if (message is Message.Assistant) {
                message.toolCalls?.let { calls ->
                    calls.forEach { call ->
                        val tool = tools.find { it.name == call.name } ?: return@forEach
                        println("[Agent] Accessing tool ${tool.name}")

                        if (tool.name == PageLoaderTool.NAME) {
                            nmbOfPageLookups++
                            if (nmbOfPageLookups >= MAX_LOAD_PAGES) {
                                currentMessages += Message.Tool("Number of load page tool calls exceeded. Cannot load any more pages.", call.toolCallId)
                                return@forEach
                            }

                        }

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

        const val MAX_LOAD_PAGES = 10
    }
}