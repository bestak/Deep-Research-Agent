# Deep Research Agent

<img src="https://github.com/bestak/Deep-Research-Agent/actions/workflows/kotlin.yml/badge.svg">

A Kotlin-based AI Deep Research agent that can autonomously plan, search, and synthesize information from the web using OpenAI models and custom tools.

---

## Features

- **Multi-step reasoning:** Breaks down user queries into actionable research plans.
- **Tool-based agent architecture:** Integrates web search and page loading tools to retrieve real-world data.
- **Extensible:** Easily add new tools or modify planning strategies.
- **Pre-parsing with fast models:** Uses lightweight models to generate detailed research instructions before executing full research.

## Architecture
```text
User Query
↓
Initial planner (fast LLM) → Structured Research Plan
↓
Research Agents (main LLM) for each Research Step
↓
Tool Execution: search_web, load_page
↓
Synthesis → Final Report
```

## Installation

1. Clone the repository:

```bash
git clone git@github.com:bestak/Deep-Research-Agent.git
cd deep-research-agent
```
2. Add your OpenAI API key and Brave Search API key to `.env`:
```
OPENAI_API_KEY=...
BRAVE_API_KEY=...
```
4. Build the project with Gradle:

```bash
./gradlew build
```

## Usage

```text
Usage: query [<options>] <query>

Options:
  --initial-model=<text>  The initial pre-processing LLM model
  --agent-model=<text>    The agent LLM model
  -h, --help              Show this message and exit

Arguments:
  <query>  The query to research using Deep Research agents
```

For example, run the agent with a user query:
```bash
./gradlew run --args="'Find recent research comparing Kotlin coroutines with Python async/await'"
```

## Examples
### Query 1 (Plan a Kyoto trip)
Plan a 5-day trip to Kyoto, Japan, for someone interested in culture and food.
Find must-see sights, museums, and cultural experiences.
Identify popular local restaurants or street food areas.
Provide a suggested day-by-day itinerary that balances sightseeing and meals.

See [query 1 results](results/kyoto.md).

### Query 2 (JetBrains)
What is the company Jetbrains from the Czech Republic? 
What are they doing? Describe this business based on the available information.

See [query 2 results](results/jetbrains.md).


