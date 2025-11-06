package cz.bestak.deepresearch.app

import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.model.ResearchPlan
import cz.bestak.deepresearch.domain.services.InitialPlanParser
import cz.bestak.deepresearch.service.llm.LLMService

class InitialPlanService(
    private val fastLLM: LLMService,
    private val instructionPrompt: String,
    private val initialPlanParser: InitialPlanParser
) {

    suspend fun create(userQuery: String): ResearchPlan {
//        val result = fastLLM.complete(
//            listOf(
//                Message.System(instructionPrompt),
//                Message.User(userQuery)
//            )
//        )
        val res = """
            {
              "steps" : [ {
                "title" : "Understand Kotlin's Coroutine Model",
                "description" : "Research and understand how Kotlin's Coroutine model works, including concepts like suspend functions, coroutine builders, and CoroutineScope."
              }, {
                "title" : "Understand Python's Async/Await",
                "description" : "Research and understand how Python's Async/Await mechanism works, including asyncio library, async functions, and event loops."
              }, {
                "title" : "Comparison of Syntax and Usage",
                "description" : "Compare the syntax and usage of Kotlin's Coroutines with Python's Async/Await in terms of how concurrency and asynchronous programming are implemented."
              }, {
                "title" : "Execution Model Differences",
                "description" : "Investigate and outline the differences in execution models between Kotlin's Coroutines and Python's Async/Await, including how they handle tasks, synchronization, and blocking operations."
              }, {
                "title" : "Performance and Efficiency",
                "description" : "Explore and compare the performance and efficiency aspects of Kotlin's Coroutines and Python's Async/Await in terms of context switching, overhead, and resource utilization."
              } ]
            }
        """.trimIndent()
        return initialPlanParser.parse(res)
//        return initialPlanParser.parse(result.messages.last().content)
    }
}