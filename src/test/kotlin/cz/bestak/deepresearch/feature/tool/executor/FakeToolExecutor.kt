package cz.bestak.deepresearch.feature.tool.executor


class FakeToolExecutor(override val name: String) : ToolExecutor {
    val executedCalls = mutableListOf<Map<String, String>>()
    override suspend fun execute(arguments: Map<String, String>): String {
        executedCalls += arguments
        return "Executed $name with args ${arguments.entries.joinToString()}"
    }
}