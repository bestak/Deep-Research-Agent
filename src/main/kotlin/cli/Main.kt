package cz.bestak.deepresearch.cli

import cz.bestak.deepresearch.core.di.coreModule
import cz.bestak.deepresearch.feature.agent.DeepResearchAgent
import cz.bestak.deepresearch.feature.agent.di.agentModule
import cz.bestak.deepresearch.feature.llm.di.llmModule
import cz.bestak.deepresearch.feature.tool.di.toolModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

object Injector : KoinComponent {
    val deepResearchAgent: DeepResearchAgent by inject()
}

suspend fun main() {
    startKoin {
        modules(listOf(
            coreModule,
            agentModule,
            llmModule,
            toolModule
        ))
    }

    val deepResearch = Injector.deepResearchAgent

    val query = "What is the company Jetbrains from Czech Republic? What are they doing? Describe this business based on the available information."
//    val query = "Plan a 5-day trip to Kyoto, Japan, for someone interested in culture and food.\n" +
//            "- Find must-see sights, museums, and cultural experiences.\n" +
//            "- Identify popular local restaurants or street food areas.\n" +
//            "- Provide a suggested day-by-day itinerary that balances sightseeing and meals."
//    val query = "Investigate the recent advances in AI for protein folding (2023–2025). \n" +
//            "- Summarize key breakthroughs, algorithms, and research papers.\n" +
//            "- Explain implications for drug discovery and biotechnology."
    deepResearch.run(query)


}