package cz.bestak.deepresearch.app

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.services.InitialPlanParser
import cz.bestak.deepresearch.domain.prompts.AgentInstructions
import cz.bestak.deepresearch.domain.services.ResearchAgent
import cz.bestak.deepresearch.domain.services.tool.BrowserTool
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

        val plan = planCreator.create(query)
        print("Plan created: ${plan.steps}")

//        val researchAgentService = ResearchAgentService()

        val tools = listOf(
            BrowserTool()
        )
        val researchAgent = ResearchAgent(fastLLM, tools)
        val messages = listOf(
            Message.System(AgentInstructions.deepResearchSystemPrompt),
            Message.User("Execute this step (1 / ${plan.steps.size}): ${plan.steps[0].title}: ${plan.steps[0].description}")
        )
        val res = researchAgent.run(messages)
        print(res)
        return ""
    }

}