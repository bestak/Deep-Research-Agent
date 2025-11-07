package cz.bestak.deepresearch.cli

import com.github.ajalt.clikt.core.main
import cz.bestak.deepresearch.cli.Injector.queryCommand
import cz.bestak.deepresearch.core.di.coreModule
import cz.bestak.deepresearch.feature.agent.di.agentModule
import cz.bestak.deepresearch.feature.llm.di.llmModule
import cz.bestak.deepresearch.feature.tool.di.toolModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

object Injector : KoinComponent {
    val queryCommand: QueryCommand by inject()
}

fun main(args: Array<String>) {
    startKoin {
        modules(listOf(
            coreModule,
            agentModule,
            llmModule,
            toolModule
        ))
    }

    queryCommand.main(args)
}
