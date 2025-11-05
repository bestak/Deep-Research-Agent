package cz.bestak.deepresearch.domain.parser

import cz.bestak.deepresearch.domain.model.ResearchPlan

class InitialPlanParser {
    fun parse(result: String): ResearchPlan {
        print(result)
        return ResearchPlan(emptyList())
    }
}