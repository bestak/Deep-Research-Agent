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
            return MISSING_ARGUMENT
        }
        if (url.isBlank()) {
            return ARGUMENT_EMPTY
        }
        println("[Load page] Opening page: $url")
        val pageContent = webPageLoaderService.load(url) ?: NO_CONTENT
        return Json.encodeToString(pageContent)
    }

    companion object {
        const val MISSING_ARGUMENT = "No `url` argument passed for the tool `${PageLoaderTool.NAME}`."
        const val ARGUMENT_EMPTY = "Argument `url` of the tool `${PageLoaderTool.NAME}` is empty."
        const val NO_CONTENT = "Loading the page content was unsuccessful."
    }
}