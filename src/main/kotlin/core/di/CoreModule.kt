package cz.bestak.deepresearch.core.di

import cz.bestak.deepresearch.service.http.HttpClient
import cz.bestak.deepresearch.service.http.KtorHttpClient
import org.koin.dsl.module

val coreModule = module {
    single<HttpClient> {
        KtorHttpClient()
    }
}