package cz.bestak.deepresearch.app

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import io.github.cdimascio.dotenv.dotenv
import kotlin.time.Duration.Companion.seconds

/*

Orchestrator:
1. Preprocess prompt to a plan, can use keywords to search something.
2. Main agent loop
(3. Possibly summarize)

Main agent loop:
1. Store context and run prompt
2. Allow search and load tools
3. Return final summary
 */

suspend fun main() {
    val deepResearch = DeepResearchAgent()

//    val query = "Compare Kotlin’s coroutine model with Python’s async/await"
//    deepResearch.run(query)

    val service = BraveSearchService()
    val res = service.search("Kotlin Ktor")
    print(res)


}