package cz.bestak.deepresearch.feature.agent.planner

import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import cz.bestak.deepresearch.feature.agent.domain.ResearchStep
import cz.bestak.deepresearch.feature.llm.service.FakeLLMService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class InitialPlanServiceTest {

    private lateinit var underTest: InitialPlanService

    @BeforeTest
    fun beforeTest() {
        underTest = InitialPlanService(initialPlanParser = InitialPlanParser())
    }

    @Test
    fun `should create initial plan`() = runBlocking {
        val fakeLLM = FakeLLMService(
            stepContent = mapOf(
                1 to FAKE_PLAN_JSON
            )
        )

        val plan = underTest.create(fakeLLM, "User query")

        val expectedPlan = ResearchPlan(
            steps = listOf(
                ResearchStep("Step 1", "Description 1"),
                ResearchStep("Step 2", "Description 2")
            )
        )
        assertEquals(expectedPlan, plan)
    }


    @Test
    fun `should propagate parser exceptions`() = runBlocking {
        val fakeLLM = FakeLLMService(stepContent = mapOf(1 to FAKE_PLAN_JSON.take(10)))

        val e = assertFailsWith<Exception> {
            underTest.create(fakeLLM, "User query")
        }
    }

    companion object {
        val FAKE_PLAN_JSON = """
        {
            "steps": [
                { "title": "Step 1", "description": "Description 1" },
                { "title": "Step 2", "description": "Description 2" }
            ]
        }
        """.trimIndent()
    }
}