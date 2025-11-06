package cz.bestak.deepresearch.feature.tool.executor

interface ToolExecutor {
    val name: String

    suspend fun execute(arguments: Map<String, String>): String
}