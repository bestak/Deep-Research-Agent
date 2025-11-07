package cz.bestak.deepresearch.feature.agent.di

import cz.bestak.deepresearch.feature.agent.DeepResearchAgent
import cz.bestak.deepresearch.feature.agent.executor.ResearchAgent
import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentImpl
import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentService
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanParser
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanService
import cz.bestak.deepresearch.feature.tool.BrowserTool
import cz.bestak.deepresearch.feature.tool.PageLoaderTool
import org.koin.dsl.module

val agentModule = module {
    single {
        DeepResearchAgent(
            initialPlanService = get(),
            researchAgentService = get(),
            modelProvider = get()
        )
    }

    single {
        InitialPlanService(initialPlanParser = get())
    }

    factory<ResearchAgent> {
        ResearchAgentImpl(
            tools = listOf(
                get<BrowserTool>(),
                get<PageLoaderTool>()
            ),
            registry = get(),
        )
    }

    factory {
        ResearchAgentService(
            researchAgent = get()
        )
    }

    factory {
        InitialPlanParser()
    }
}