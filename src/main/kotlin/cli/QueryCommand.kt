package cz.bestak.deepresearch.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import cz.bestak.deepresearch.feature.agent.DeepResearchAgent
import kotlinx.coroutines.runBlocking

class QueryCommand(
    private val deepResearchAgent: DeepResearchAgent
): CliktCommand() {
    val query: String by argument().help("The query to research using Deep Research agents")
    val initialModel: String by option().help("The initial pre-processing LLM model").default("gpt-3.5-turbo")
    val agentModel: String by option().help("The agent LLM model").default("gpt-5-mini-2025-08-07")

    override fun run() {
        runBlocking {
            deepResearchAgent.run(
                query = query,
                fastModel = initialModel,
                agentModel = agentModel
            )
        }
    }
}