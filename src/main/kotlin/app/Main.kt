package cz.bestak.deepresearch.app

import cz.bestak.deepresearch.domain.services.tool.BrowserTool
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import cz.bestak.deepresearch.service.http.HttpClient

/*

Orchestrator:
1. Preprocess prompt to a plan, can use keywords to search something.
2. Main agent loop
(3. Possibly summarize)

Main agent loop:
1. Store context and run prompt
2. Allow search and load tools
3. Return final summary
 */

suspend fun main() {
    val deepResearch = DeepResearchAgent()

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