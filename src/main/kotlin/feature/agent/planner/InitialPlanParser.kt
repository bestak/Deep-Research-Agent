package cz.bestak.deepresearch.feature.agent.planner

import cz.bestak.deepresearch.feature.agent.domain.ResearchPlan
import kotlinx.serialization.json.Json

class InitialPlanParser {
    fun parse(result: String): ResearchPlan {
        return Json.decodeFromString<ResearchPlan>(result)
    }
}