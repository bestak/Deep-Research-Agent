package cz.bestak.deepresearch.feature.agent.executor

import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.agent.domain.ResearchStep
import cz.bestak.deepresearch.feature.llm.domain.Message
import cz.bestak.deepresearch.feature.llm.service.FakeLLMService
import cz.bestak.deepresearch.feature.llm.service.LLMService
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ResearchAgentServiceTest {

    private val llm: LLMService = FakeLLMService()

    private lateinit var fakeResearchAgent: FakeResearchAgent
    private lateinit var underTest: ResearchAgentService

    @BeforeTest
    fun beforeTest() {
        fakeResearchAgent = FakeResearchAgent()
        underTest = ResearchAgentService(fakeResearchAgent)
    }

    @Test
    fun `should execute all research plan steps and summarize`() = runBlocking {
        val plan = ResearchPlan(
            steps = listOf(
                ResearchStep("Step 1", "Find info about topic A"),
                ResearchStep("Step 2", "Analyze results about topic B")
            )
        )

        val result = underTest.executePlan(llm, plan)

        assertEquals(FakeResearchAgent.getSummary(2), result)
        assertEquals(3, fakeResearchAgent.runCalls.size)
    }

    @Test
    fun `should create correct message sequence`() = runBlocking {
        val plan = ResearchPlan(
            steps = listOf(
                ResearchStep("Step 1", "Explore reasoning capabilities")
            )
        )

        underTest.executePlan(llm, plan)

        val messages = fakeResearchAgent.runCalls.last().messages
        assertEquals(4, messages.size)
        assertEquals(messages[0], Message.System(AgentInstructions.getDeepResearchSystemPrompt(ResearchAgentService.MAX_STEP_COUNT)))
        assertEquals(messages[1], Message.User(underTest.getStepIntroMessage(0, plan, plan.steps[0])))
        assertEquals(messages[2], Message.Assistant(FakeResearchAgent.STEP_RESULT))
        assertEquals(messages[3], Message.User(AgentInstructions.summarizeResult))
    }

    @Test
    fun `getStepIntroMessage should return formatted message correctly`() {
        val steps = listOf(
            ResearchStep("Step 1", "Learn about Kotlin coroutines"),
            ResearchStep("Step 2", "Understand suspend functions")
        )
        val plan = ResearchPlan(steps = steps)
        val step = steps[0]

        val message = underTest.getStepIntroMessage(0, plan, step)

        val expected = "Execute this step (1 / 2): Step 1: Learn about Kotlin coroutines"
        assertEquals(expected, message)
    }
}