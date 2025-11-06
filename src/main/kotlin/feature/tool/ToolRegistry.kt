package cz.bestak.deepresearch.feature.tool

import cz.bestak.deepresearch.feature.tool.executor.ToolExecutor

class ToolRegistry(
    private val executors: List<ToolExecutor>
) {
    fun findByName(name: String): ToolExecutor {
        val executor = executors.find { it.name == name }
        if (executor == null) {
            throw Exception("No tool executor found for name $name")
        }
        return executor
    }
}