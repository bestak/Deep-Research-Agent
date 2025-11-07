package cz.bestak.deepresearch.feature.tool.di

import cz.bestak.deepresearch.feature.tool.ToolRegistry
import cz.bestak.deepresearch.feature.tool.connectors.browser.BrowserSearchService
import cz.bestak.deepresearch.feature.tool.connectors.pageloader.HttpWebPageLoaderService
import cz.bestak.deepresearch.feature.tool.executor.specific.BrowserToolExecutor
import cz.bestak.deepresearch.feature.tool.executor.specific.PageLoaderToolExecutor
import cz.bestak.deepresearch.feature.tool.connectors.browser.brave.BraveSearchService
import cz.bestak.deepresearch.feature.tool.connectors.pageloader.WebPageLoaderService
import org.koin.dsl.module

val toolModule = module {

    single<BrowserSearchService> {
        BraveSearchService(get())
    }

    single<WebPageLoaderService> {
        HttpWebPageLoaderService(get())
    }

    single {
        BrowserToolExecutor(get())
    }

    single {
        PageLoaderToolExecutor(
            webPageLoaderService = get()
        )
    }

    single {
        val executors = listOf(
            get<BrowserToolExecutor>(),
            get<PageLoaderToolExecutor>()
        )
        ToolRegistry(executors)
    }

}