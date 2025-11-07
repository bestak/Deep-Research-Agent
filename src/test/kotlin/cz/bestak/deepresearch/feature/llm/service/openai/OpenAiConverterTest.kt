package cz.bestak.deepresearch.feature.llm.service.openai

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.ToolId
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.domain.ToolCall
import com.aallam.openai.api.chat.ToolCall as OAIToolCall
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenAiConverterTest {

    private val converter = OpenAiConverter()

    @Test
    fun `should map all Message types to OAI ChatMessage`() {
        val system = converter.toChatMessage(Message.System("sys"))
        val user = converter.toChatMessage(Message.User("usr"))
        val assistant = converter.toChatMessage(Message.Assistant("asst", listOf(
            ToolCall("1", "browser", mapOf("q" to "Kotlin"))
        )))
        val tool = converter.toChatMessage(Message.Tool("tool content", "tool-id"))

        assertEquals(ChatRole.System, system.role)
        assertEquals(ChatRole.User, user.role)
        assertEquals(ChatRole.Tool, tool.role)

        assertEquals(ChatRole.Assistant, assistant.role)
        assertEquals(1, assistant.toolCalls?.size)
        assertEquals("browser", (assistant.toolCalls?.first() as? OAIToolCall.Function)?.function?.name)
    }

    @Test
    fun `should map all ChatMessage types to Message`() {
        val chatSystem = ChatMessage(role = ChatRole.System, content = "sys")
        val chatUser = ChatMessage(role = ChatRole.User, content = "usr")
        val chatAssistant = ChatMessage(
            role = ChatRole.Assistant,
            content = "asst",
            toolCalls = listOf(
                OAIToolCall.Function(
                    id = ToolId("1"),
                    function = FunctionCall(
                        nameOrNull = "browser",
                        argumentsOrNull = """{"q":"Kotlin"}"""
                    )
                )
            )
        )
        val chatTool = ChatMessage(
            role = ChatRole.Tool,
            content = "tool content",
            toolCallId = ToolId("tool-id")
        )

        val system = converter.toMessage(chatSystem)
        val user = converter.toMessage(chatUser)
        val assistant = converter.toMessage(chatAssistant)
        val tool = converter.toMessage(chatTool)

        assertTrue(system is Message.System)
        assertTrue(user is Message.User)
        assertTrue(assistant is Message.Assistant)
        assertEquals("browser", (assistant as Message.Assistant).toolCalls?.first()?.name)
        assertTrue(tool is Message.Tool)
        assertEquals("tool-id", (tool as Message.Tool).toolCallId)
    }
}