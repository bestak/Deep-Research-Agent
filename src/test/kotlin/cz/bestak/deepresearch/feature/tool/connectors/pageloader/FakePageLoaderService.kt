package cz.bestak.deepresearch.feature.tool.connectors.pageloader

class FakePageLoaderService : WebPageLoaderService {
    val urls = mutableListOf<String>()
    var resultToReturn: String? = "RESULT"

    override suspend fun load(url: String): String? {
        urls += url
        return resultToReturn
    }
}