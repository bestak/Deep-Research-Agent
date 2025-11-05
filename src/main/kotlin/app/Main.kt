package cz.bestak.deepresearch.app

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
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

    val query = "Compare Kotlin’s coroutine model with Python’s async/await"
    deepResearch.run(query)

//
//    print(openAI.models())
//
//    val chatCompletionRequest = ChatCompletionRequest(
//        model = ModelId("gpt-3.5-turbo"),
//        messages = listOf(
//            ChatMessage(
//                role = ChatRole.System,
//                content = "You are a helpful assistant!"
//            ),
//            ChatMessage(
//                role = ChatRole.User,
//                content = "Hello! What can you do?"
//            )
//        ),
//    )
//    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
//    print(completion.choices.first().message)

}