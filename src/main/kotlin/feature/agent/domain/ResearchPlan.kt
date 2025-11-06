package cz.bestak.deepresearch.feature.agent.domain

import kotlinx.serialization.Serializable

@Serializable
data class ResearchPlan(
    val steps: List<ResearchStep>
)

@Serializable
data class ResearchStep(
    val title: String,
    val description: String,
)