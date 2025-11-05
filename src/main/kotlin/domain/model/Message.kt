package cz.bestak.deepresearch.domain.model

data class Message(val role: Role, val content: String)

enum class Role {
    System,
    User,
    Tool
}