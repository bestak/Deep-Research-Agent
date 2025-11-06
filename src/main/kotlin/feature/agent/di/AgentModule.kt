package cz.bestak.deepresearch.feature.agent.di

import cz.bestak.deepresearch.feature.agent.DeepResearchAgent
import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentService
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanParser
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanService
import cz.bestak.deepresearch.service.http.HttpClient
import cz.bestak.deepresearch.service.http.KtorHttpClient
import org.koin.dsl.module

val agentModule = module {
    single {
        DeepResearchAgent(
            initialPlanService = get(),
            researchAgentService = get()
        )
    }

    single {
        InitialPlanService(initialPlanParser = get())
    }

    single {
        ResearchAgentService(toolRegistry = get())
    }

    factory {
        InitialPlanParser()
    }
}