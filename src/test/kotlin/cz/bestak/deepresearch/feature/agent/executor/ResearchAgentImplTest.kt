package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.domain.ToolCall
import cz.bestak.deepresearch.feature.llm.service.FakeLLMService
import cz.bestak.deepresearch.feature.tool.BrowserTool
import cz.bestak.deepresearch.feature.tool.ToolRegistry
import cz.bestak.deepresearch.feature.tool.executor.FakeToolExecutor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class ResearchAgentImplTest {

    @Test
    fun `should execute a single step and return content without tool calls`() = runBlocking {
        val fakeLLM = FakeLLMService()
        val fakeRegistry = ToolRegistry(emptyList())
        val agent = ResearchAgentImpl(emptyList(), fakeRegistry)

        val messages = listOf(Message.System("system"))
        val result = agent.run(fakeLLM, messages, maxSteps = 5)

        assertTrue(result.response.contains("Step 1 content"))
        assertEquals(1, fakeLLM.callCount)
    }

    @Test
    fun `should execute tool call if LLM requests it`() = runBlocking {
        val toolExecutor = FakeToolExecutor("browser")
        val fakeRegistry = ToolRegistry(listOf(toolExecutor))
        val tool = BrowserTool()
        val agent = ResearchAgentImpl(listOf(tool), fakeRegistry)
        val toolCall = ToolCall(name = tool.name, arguments = mapOf("query" to "Kotlin"), toolCallId = "1")
        val fakeLLM = FakeLLMService(messagesToToolCalls = mapOf(1 to listOf(toolCall)))

        val messages = listOf<Message>(Message.System("system"))
        agent.run(fakeLLM, messages, maxSteps = 5)

        assertEquals(1, toolExecutor.executedCalls.size)
        assertEquals("Kotlin", toolExecutor.executedCalls.first()["query"])
    }

    @Test
    fun `should generate correct message sequence`() = runBlocking {
        val toolExecutor = FakeToolExecutor("browser")
        val fakeRegistry = ToolRegistry(listOf(toolExecutor))
        val tool = BrowserTool()
        val agent = ResearchAgentImpl(listOf(tool), fakeRegistry)
        val toolCall = ToolCall(name = tool.name, arguments = mapOf("query" to "Kotlin"), toolCallId = "1")
        val fakeLLM = FakeLLMService(
            messagesToToolCalls = mapOf(1 to listOf(toolCall)),
            stepContent = mapOf(1 to "")
        )

        val initialMessages = listOf<Message>(Message.System("system"))
        val result = agent.run(fakeLLM, initialMessages, maxSteps = 5)
        val messages = result.messages

        assertEquals(6, messages.size)
        assertEquals(messages[0], initialMessages[0])
        assertEquals(messages[1], Message.User(AgentInstructions.beginningOfStep(5)))
        assertEquals(messages[2], Message.Assistant("", listOf(toolCall)))
        assertEquals(messages[3], Message.Tool("Executed browser with args query=Kotlin", toolCallId = "1"))
        assertEquals(messages[4], Message.User(AgentInstructions.beginningOfStep(4)))
        assert(messages[5] is Message.Assistant)
        assert((messages[5] as Message.Assistant).content.contains(ResearchAgentImpl.END_STEP_TAG))

        // Check that Tool was executed with correct arguments
        assertEquals(1, toolExecutor.executedCalls.size)
        assertEquals("Kotlin", toolExecutor.executedCalls.first()["query"])
    }
}