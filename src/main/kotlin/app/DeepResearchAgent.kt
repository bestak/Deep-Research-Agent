package cz.bestak.deepresearch.app

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.domain.parser.InitialPlanParser
import cz.bestak.deepresearch.domain.prompts.AgentInstructions
import cz.bestak.deepresearch.service.llm.openai.OpenAiLLMService
import io.github.cdimascio.dotenv.dotenv
import kotlin.time.Duration.Companion.seconds

class DeepResearchAgent {

    suspend fun run(query: String): String {

        val openAI = OpenAI(
            token = dotenv()["OPENAI_API_KEY"],
            timeout = Timeout(socket = 60.seconds),
        )

        val fastLLM = OpenAiLLMService(openAI, ModelId("gpt-3.5-turbo"))
        val planCreator = InitialPlanService(
            fastLLM = fastLLM,
            instructionPrompt = AgentInstructions.preProcessUserPrompt,
            initialPlanParser = InitialPlanParser()
        )

        val res = planCreator.create(query)
        print(res.steps)
        return ""
    }

}