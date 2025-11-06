package cz.bestak.deepresearch.service.pageloader

interface WebPageLoaderService {

    suspend fun load(url: String): String

}