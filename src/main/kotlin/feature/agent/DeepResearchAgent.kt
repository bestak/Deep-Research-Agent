package cz.bestak.deepresearch.feature.agent

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.feature.agent.domain.AgentInstructions
import cz.bestak.deepresearch.feature.agent.executor.ResearchAgentService
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanParser
import cz.bestak.deepresearch.feature.agent.planner.InitialPlanService
import cz.bestak.deepresearch.feature.llm.service.openai.OpenAiLLMService
import io.github.cdimascio.dotenv.dotenv
import kotlin.time.Duration.Companion.seconds

class DeepResearchAgent(
    private val initialPlanService: InitialPlanService,
    private val researchAgentService: ResearchAgentService
) {

    suspend fun run(query: String): String {

        val openAI = OpenAI(
            token = dotenv()["OPENAI_API_KEY"],
            timeout = Timeout(socket = 60.seconds),
            logging = LoggingConfig(logLevel = LogLevel.None)
        )

        val fastLLM = OpenAiLLMService(openAI, ModelId("gpt-3.5-turbo"))
        val plan = initialPlanService.create(fastLLM, query)

        val agentLLM = OpenAiLLMService(openAI, ModelId("gpt-5-mini-2025-08-07"))
        val result = researchAgentService.executePlan(agentLLM, plan)

        println("=".repeat(80))
        println(result)
        return result
    }

}
