package cz.bestak.deepresearch.feature.agent.planner

import kotlin.test.Test
import kotlin.test.assertEquals

class InitialPlanParserTest {

    @Test
    fun `parse simple JSON`() {
        val jsonInput = """
        {
            "steps": [
                { "title": "Step 1", "description": "Description 1" },
                { "title": "Step 2", "description": "Description 2" }
            ]
        }
        """.trimIndent()

        val plan = InitialPlanParser().parse(jsonInput)

        assertEquals(2, plan.steps.size)
        assertEquals("Step 1", plan.steps[0].title)
        assertEquals("Description 1", plan.steps[0].description)
        assertEquals("Step 2", plan.steps[1].title)
        assertEquals("Description 2", plan.steps[1].description)
    }

}