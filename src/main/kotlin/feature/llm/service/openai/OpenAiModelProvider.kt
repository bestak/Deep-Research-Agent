package cz.bestak.deepresearch.feature.llm.service.openai

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.feature.llm.service.LLMModelProvider
import cz.bestak.deepresearch.feature.llm.service.LLMService

class OpenAiModelProvider(
    private val openAI: OpenAI
): LLMModelProvider {
    override fun getModel(id: String): LLMService {
        return OpenAiLLMService(openAI, ModelId(id))
    }
}