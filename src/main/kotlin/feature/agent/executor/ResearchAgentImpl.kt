package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.tool.Tool
import cz.bestak.deepresearch.feature.llm.service.LLMService
import cz.bestak.deepresearch.feature.tool.PageLoaderTool
import cz.bestak.deepresearch.feature.tool.ToolRegistry

class ResearchAgentImpl(
    private val tools: List<Tool>,
    private val registry: ToolRegistry
): ResearchAgent {

    private var nmbOfPageLookups = 0

    override suspend fun run(llm: LLMService, messages: List<Message>, maxSteps: Int): ResearchAgent.Result {
        val currentMessages = messages.toMutableList()
        nmbOfPageLookups = 0

        (0..maxSteps).forEach { step ->
            println("[Agent] Thinking...")
            currentMessages += Message.User(AgentInstructions.beginningOfStep(maxSteps - step))
            val message = llm.complete(currentMessages, tools)
            currentMessages += message
            println("[Agent] ${message.content}")

            if (message is Message.Assistant) {
                currentMessages.addAll(executeToolCalls(message))
            }
            if (message.content.contains(END_STEP_TAG)) {
                println("[Agent] Ending step, found end tag")
                return ResearchAgent.Result(
                    response = message.content.replace(END_STEP_TAG, ""),
                    messages = currentMessages
                )
            }
        }
        return ResearchAgent.Result(
            response = STEP_INVALID,
            messages = currentMessages
        )
    }

    private suspend fun executeToolCalls(message: Message.Assistant): List<Message> {
        val newMessages = mutableListOf<Message>()
        message.toolCalls?.let { calls ->
            calls.forEach { call ->
                val tool = tools.find { it.name == call.name } ?: return@forEach
                println("[Agent] Accessing tool ${tool.name}")

                if (tool.name == PageLoaderTool.NAME) {
                    nmbOfPageLookups++
                    if (nmbOfPageLookups >= MAX_LOAD_PAGES) {
                        newMessages += Message.Tool("Number of load page tool calls exceeded. Cannot load any more pages.", call.toolCallId)
                        return@forEach
                    }
                }

                val executor = registry.findByName(tool.name)
                executor.execute(call.arguments).let { toolRes ->
                    newMessages += Message.Tool(toolRes, call.toolCallId)
                }
            }
        }
        return newMessages
    }

    companion object {
        const val STEP_INVALID = "Research step wasn't completed successfully, call limit reached."
        const val END_STEP_TAG = "<END_OF_STEP>"

        const val MAX_LOAD_PAGES = 10
    }
}