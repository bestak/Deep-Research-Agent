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

    val query = "Compare Kotlin’s coroutine model with Python’s async/await"
    deepResearch.run(query)


}