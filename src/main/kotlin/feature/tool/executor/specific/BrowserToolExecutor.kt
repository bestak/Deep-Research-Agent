package cz.bestak.deepresearch.feature.tool.executor.specific

import cz.bestak.deepresearch.feature.tool.BrowserTool
import cz.bestak.deepresearch.feature.tool.connectors.browser.BrowserSearchService
import cz.bestak.deepresearch.feature.tool.executor.ToolExecutor
import kotlinx.serialization.json.Json

class BrowserToolExecutor(
    private val browserSearchService: BrowserSearchService
): ToolExecutor {
    override val name = BrowserTool.NAME

    override suspend fun execute(arguments: Map<String, String>): String {
        val query = arguments.getOrElse("query") {
            return "No `query` argument passed for the tool `$name`."
        }
        if (query.isBlank()) {
            return "Argument `query` of the tool `$name` is empty."
        }
        println("[Browser] Searching for: $query")
        val browserResults = browserSearchService.search(query)
        return Json.encodeToString(browserResults)
    }
}