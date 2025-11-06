package cz.bestak.deepresearch.feature.tool.di

import cz.bestak.deepresearch.feature.tool.ToolRegistry
import cz.bestak.deepresearch.feature.tool.connectors.pageloader.HttpWebPageLoaderService
import cz.bestak.deepresearch.feature.tool.executor.ToolExecutor
import cz.bestak.deepresearch.feature.tool.executor.specific.BrowserToolExecutor
import cz.bestak.deepresearch.feature.tool.executor.specific.PageLoaderToolExecutor
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import cz.bestak.deepresearch.service.http.HttpClient
import cz.bestak.deepresearch.service.http.KtorHttpClient
import org.koin.dsl.module

val toolModule = module {

    single {
        BraveSearchService(get())
    }

    single {
        HttpWebPageLoaderService(get())
    }

    single {
        BrowserToolExecutor(get())
    }

    single {
        PageLoaderToolExecutor(get())
    }

    single {
        val executors = listOf(
            get<BrowserToolExecutor>(),
            get<PageLoaderToolExecutor>()
        )
        ToolRegistry(executors)
    }

}