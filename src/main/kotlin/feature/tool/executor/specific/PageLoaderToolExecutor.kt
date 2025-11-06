package cz.bestak.deepresearch.feature.tool.executor.specific

import cz.bestak.deepresearch.feature.tool.PageLoaderTool
import cz.bestak.deepresearch.feature.tool.connectors.pageloader.WebPageLoaderService
import cz.bestak.deepresearch.feature.tool.executor.ToolExecutor
import kotlinx.serialization.json.Json

class PageLoaderToolExecutor(
    private val webPageLoaderService: WebPageLoaderService
): ToolExecutor {
    override val name: String = PageLoaderTool.NAME

    override suspend fun execute(arguments: Map<String, String>): String {
        val url = arguments.getOrElse("url") {
            return "No `url` argument passed for the tool `$name`."
        }
        if (url.isBlank()) {
            return "Argument `url` of the tool `$name` is empty."
        }
        println("[Load page] Opening page: $url")
        val browserResults = webPageLoaderService.load(url)
        return Json.encodeToString(browserResults)
    }
}