package cz.bestak.deepresearch.feature.llm.di

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.feature.llm.service.LLMModelProvider
import cz.bestak.deepresearch.feature.llm.service.openai.OpenAiConverter
import cz.bestak.deepresearch.feature.llm.service.openai.OpenAiModelProvider
import cz.bestak.deepresearch.service.http.HttpClient
import cz.bestak.deepresearch.service.http.KtorHttpClient
import io.github.cdimascio.dotenv.dotenv
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val llmModule = module {

    factory {
        OpenAI(
            token = dotenv()["OPENAI_API_KEY"],
            timeout = Timeout(socket = 60.seconds),
            logging = LoggingConfig(logLevel = LogLevel.None)
        )
    }

    factory {
        OpenAiConverter()
    }

    single<LLMModelProvider> {
        OpenAiModelProvider(
            openAI = get(),
            openAiConverter = get(),
        )
    }

}