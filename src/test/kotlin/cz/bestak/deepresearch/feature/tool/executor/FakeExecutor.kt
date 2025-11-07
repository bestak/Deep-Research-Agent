package cz.bestak.deepresearch.feature.tool.executor

class FakeExecutor(override val name: String) : ToolExecutor {
    override suspend fun execute(arguments: Map<String, String>): String = "Executed $name"
}