package cz.bestak.deepresearch.domain.model

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