package cz.bestak.deepresearch.domain.parser

import cz.bestak.deepresearch.domain.model.ResearchPlan
import kotlinx.serialization.json.Json

class InitialPlanParser {
    fun parse(result: String): ResearchPlan {
        return Json.decodeFromString<ResearchPlan>(result)
    }
}