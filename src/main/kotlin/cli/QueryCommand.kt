package cz.bestak.deepresearch.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import cz.bestak.deepresearch.feature.agent.DeepResearchAgent
import kotlinx.coroutines.runBlocking

class QueryCommand(
    private val deepResearchAgent: DeepResearchAgent
): CliktCommand() {
    val query: String by argument().help("The query to research using Deep Research agents")

    override fun run() {
        runBlocking {
            deepResearchAgent.run(query)
        }
    }
}