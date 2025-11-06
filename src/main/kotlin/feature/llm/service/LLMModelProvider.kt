package cz.bestak.deepresearch.feature.llm.service

interface LLMModelProvider {

    fun getModel(id: String): LLMService
}