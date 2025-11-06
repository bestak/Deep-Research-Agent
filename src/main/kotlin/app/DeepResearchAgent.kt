package cz.bestak.deepresearch.app

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import cz.bestak.deepresearch.domain.model.Message
import cz.bestak.deepresearch.domain.services.InitialPlanParser
import cz.bestak.deepresearch.domain.prompts.AgentInstructions
import cz.bestak.deepresearch.domain.services.ResearchAgent
import cz.bestak.deepresearch.domain.services.tool.BrowserTool
import cz.bestak.deepresearch.domain.services.tool.PageLoaderTool
import cz.bestak.deepresearch.service.browser.brave.BraveSearchService
import cz.bestak.deepresearch.service.http.HttpClient
import cz.bestak.deepresearch.service.http.KtorHttpClient
import cz.bestak.deepresearch.service.llm.openai.OpenAiLLMService
import cz.bestak.deepresearch.service.pageloader.HttpWebPageLoaderService
import io.github.cdimascio.dotenv.dotenv
import kotlin.time.Duration.Companion.seconds

class DeepResearchAgent {

    suspend fun run(query: String): String {

        val openAI = OpenAI(
            token = dotenv()["OPENAI_API_KEY"],
            timeout = Timeout(socket = 60.seconds),
            logging = LoggingConfig(logLevel = LogLevel.None)
        )

        val fastLLM = OpenAiLLMService(openAI, ModelId("gpt-3.5-turbo"))
        val planCreator = InitialPlanService(
            fastLLM = fastLLM,
            instructionPrompt = AgentInstructions.preProcessUserPrompt,
            initialPlanParser = InitialPlanParser()
        )

        val plan = planCreator.create(query)
        print("Plan created: ${plan.steps}")

//        val researchAgentService = ResearchAgentService()

        val client = KtorHttpClient()
        val browserSearchService = BraveSearchService(object: HttpClient {
            override suspend fun get(
                url: String,
                headers: Map<String, String>,
                params: Map<String, String>
            ): String {
                return BROWSER_RESPONSE
            }

        })
        val webPageLoaderService = HttpWebPageLoaderService(client)

        val tools = listOf(
            BrowserTool(browserSearchService),
            PageLoaderTool(webPageLoaderService)
        )

        val agentLLM = OpenAiLLMService(openAI, ModelId("gpt-5-mini-2025-08-07"))
        val researchAgent = ResearchAgent(agentLLM, tools)
        val messages = listOf(
            Message.System(AgentInstructions.deepResearchSystemPrompt),
            Message.User("Execute this step (1 / ${plan.steps.size}): ${plan.steps[0].title}: ${plan.steps[0].description}")
        )
        val res = researchAgent.run(messages)
        return ""
    }

}

private val BROWSER_RESPONSE = """
    {
      "query" : {
        "original" : "Kotlin Coroutine model explained",
        "show_strict_warning" : false,
        "is_navigational" : false,
        "is_news_breaking" : false,
        "spellcheck_off" : true,
        "country" : "us",
        "bad_results" : false,
        "should_fallback" : false,
        "postal_code" : "",
        "city" : "",
        "header_country" : "",
        "more_results_available" : true,
        "state" : ""
      },
      "mixed" : {
        "type" : "mixed",
        "main" : [ {
          "type" : "web",
          "index" : 0,
          "all" : false
        }, {
          "type" : "web",
          "index" : 1,
          "all" : false
        }, {
          "type" : "videos",
          "all" : true
        }, {
          "type" : "web",
          "index" : 2,
          "all" : false
        }, {
          "type" : "web",
          "index" : 3,
          "all" : false
        }, {
          "type" : "web",
          "index" : 4,
          "all" : false
        }, {
          "type" : "web",
          "index" : 5,
          "all" : false
        }, {
          "type" : "web",
          "index" : 6,
          "all" : false
        }, {
          "type" : "web",
          "index" : 7,
          "all" : false
        }, {
          "type" : "web",
          "index" : 8,
          "all" : false
        }, {
          "type" : "web",
          "index" : 9,
          "all" : false
        }, {
          "type" : "web",
          "index" : 10,
          "all" : false
        }, {
          "type" : "web",
          "index" : 11,
          "all" : false
        }, {
          "type" : "web",
          "index" : 12,
          "all" : false
        }, {
          "type" : "web",
          "index" : 13,
          "all" : false
        }, {
          "type" : "web",
          "index" : 14,
          "all" : false
        }, {
          "type" : "web",
          "index" : 15,
          "all" : false
        }, {
          "type" : "web",
          "index" : 16,
          "all" : false
        }, {
          "type" : "web",
          "index" : 17,
          "all" : false
        }, {
          "type" : "web",
          "index" : 18,
          "all" : false
        }, {
          "type" : "web",
          "index" : 19,
          "all" : false
        } ],
        "top" : [ ],
        "side" : [ ]
      },
      "type" : "search",
      "videos" : {
        "type" : "videos",
        "results" : [ {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=PPHkza3pZpU",
          "title" : "Kotlin Coroutines + ViewModel + LiveData for a Shopping App ...",
          "description" : "Let's upgrade our Ecommerce/Shopping app from RxJava to Kotlin Coroutines and use ViewModel and LiveData. This is a long tutorial and there will be more! The...",
          "age" : "December 14, 2019",
          "page_age" : "2019-12-14T20:19:39",
          "fetched_content_timestamp" : 1737024438,
          "video" : {
            "duration" : "24:08",
            "creator" : "DJ Malone",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/rqZ4zsxi2RKI4Wa7zGNdb-io5P2UU4yZoKkhgnR3qEA/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS9Q/UEhremEzcFpwVS9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/PPHkza3pZpU/maxresdefault.jpg"
          }
        }, {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=v2yocpEcE_g",
          "title" : "Kotlin Andriod MVVM CURD APP Tutorial : Room + Coroutines + ...",
          "description" : "Enroll to My 37 hour Advanced Android Development Course at Udemy (88% off, $12.99 only)https://www.udemy.com/course/android-architecture-componentsmvvm-with...",
          "age" : "April 7, 2020",
          "page_age" : "2020-04-07T10:30:02",
          "fetched_content_timestamp" : 1753814725,
          "video" : {
            "duration" : "01:46:00",
            "creator" : "AppDevNotes",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/k4j024bV77Ug3_BKdx9ZcMIh0ukW50kLpejzD7bowuI/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS92/MnlvY3BFY0VfZy9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/v2yocpEcE_g/maxresdefault.jpg"
          }
        }, {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=xZv885bsmGI",
          "title" : "Kotlin Coroutines : View Model Scope example - YouTube",
          "description" : "Our Kotlin Coroutines Beginner Tutorial : https://www.youtube.com/watch?v=vjt_ASNhpSE&t=96s Our Retrofit with Kotlin Coroutines Tutorial : https://www.youtub",
          "age" : "April 6, 2020",
          "page_age" : "2020-04-06T00:00:00",
          "fetched_content_timestamp" : 1602914238,
          "video" : {
            "duration" : "14:39",
            "views" : 272,
            "creator" : "AppDevNotes-Learn Android Development",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/8AOkIsC-S9rxFFduD790GG98Ax14X8AK1MdMVuVhvmQ/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS94/WnY4ODVic21HSS9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/xZv885bsmGI/maxresdefault.jpg"
          }
        }, {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=935sWXboIbk",
          "title" : "#4 Kotlin Coroutines Tutorial for Android - ViewModelScope - YouTube",
          "description" : "In this video we will move the networking code from the fragment to viewmodel, and then we will learn using viewModelScope to launch suspending function.👉 G...",
          "age" : "May 13, 2020",
          "page_age" : "2020-05-13T17:14:45",
          "fetched_content_timestamp" : 1759092646,
          "video" : {
            "duration" : "07:25",
            "creator" : "Simplified Coding",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/nHxUJhsDFEvR9ljKC4X4czE_6vbDR8CuhdvxpxuPdMM/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS85/MzVzV1hib0liay9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/935sWXboIbk/maxresdefault.jpg"
          }
        }, {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=B8ppnjGPAGE",
          "title" : "LiveData with Coroutines and Flow (Android Dev Summit '19) - YouTube",
          "description" : "LiveData is a simple lifecycle-aware observable, designed for making UIs that react to changes safely and efficiently. It can be used beyond View↔️ViewModel ...",
          "age" : "October 23, 2019",
          "page_age" : "2019-10-23T00:00:00",
          "fetched_content_timestamp" : 1606616549,
          "video" : {
            "duration" : "18:44",
            "views" : 67003,
            "creator" : "Android Developers",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/-NXP3esSLnuc_GgVzbobT3c39z0eRK7prmy38lNs-P8/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS9C/OHBwbmpHUEFHRS9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/B8ppnjGPAGE/maxresdefault.jpg"
          }
        }, {
          "type" : "video_result",
          "url" : "https://www.youtube.com/watch?v=W2c_HWthB0Y",
          "title" : "Android Tutorial 2019 #7: Implementing ViewModel w/ MutableLiveData, ...",
          "description" : "Enjoy my teaching style? Looking for a comprehensive introduction to Kotlin, Android, and Programming in general? If so, consider picking up my introductory ...",
          "age" : "April 13, 2019",
          "page_age" : "2019-04-13T00:00:00",
          "fetched_content_timestamp" : 1602913552,
          "video" : {
            "duration" : "36:51",
            "views" : 3559,
            "creator" : "Ryan M. Kay - wiseAss",
            "publisher" : "YouTube"
          },
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "youtube.com",
            "hostname" : "www.youtube.com",
            "favicon" : "https://imgs.search.brave.com/Wg4wjE5SHAargkzePU3eSLmWgVz84BEZk1SjSglJK_U/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTkyZTZiMWU3/YzU3Nzc5YjExYzUy/N2VhZTIxOWNlYjM5/ZGVjN2MyZDY4Nzdh/ZDYzMTYxNmI5N2Rk/Y2Q3N2FkNy93d3cu/eW91dHViZS5jb20v",
            "path" : "› watch"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/KGe_AL63p2QaKlDBo0d5BEZ9edq0f-GwxpFIuGsmCE4/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pLnl0/aW1nLmNvbS92aS9X/MmNfSFd0aEIwWS9t/YXhyZXNkZWZhdWx0/LmpwZw",
            "original" : "https://i.ytimg.com/vi/W2c_HWthB0Y/maxresdefault.jpg"
          }
        } ],
        "mutated_by_goggles" : false
      },
      "web" : {
        "type" : "search",
        "results" : [ {
          "title" : "Coroutines basics | Kotlin Documentation",
          "url" : "https://kotlinlang.org/docs/coroutines-basics.html",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "To create applications that perform multiple tasks at once, a concept known as concurrency, Kotlin uses coroutines. <strong>A coroutine is a suspendable computation that lets you write concurrent code in a clear, sequential style</strong>.",
          "profile" : {
            "name" : "Kotlin",
            "url" : "https://kotlinlang.org/docs/coroutines-basics.html",
            "long_name" : "kotlinlang.org",
            "img" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "generic",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "kotlinlang.org",
            "hostname" : "kotlinlang.org",
            "favicon" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8",
            "path" : "› docs  › coroutines-basics.html"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/DlHhvc3n4mfIQ9r_tKg4tbLoTBnKanEU48c5-vzzmVg/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9rb3Rs/aW5sYW5nLm9yZy9h/c3NldHMvaW1hZ2Vz/L29wZW4tZ3JhcGgv/ZG9jcy5wbmc",
            "original" : "https://kotlinlang.org/assets/images/open-graph/docs.png",
            "logo" : false
          },
          "extra_snippets" : [ "Coroutines can run concurrently with other coroutines and potentially in parallel. On the JVM and in Kotlin/Native, all concurrent code, such as coroutines, runs on threads, managed by the operating system. Coroutines can suspend their execution instead of blocking a thread.", "You can also write this.launch without the explicit this expression, as launch. These examples use explicit this expressions to highlight that it's an extension function on CoroutineScope. For more information on how lambdas with receivers work in Kotlin, see Function literals with receiver.", "If the coroutine context doesn't include a dispatcher, coroutine builders use Dispatchers.Default. The kotlinx.coroutines library includes different dispatchers for different use cases. For example, Dispatchers.Default runs coroutines on a shared pool of threads, performing work in the background, separate from the main thread.", "While the suspend keyword is part of the core Kotlin language, most coroutine features are available through the kotlinx.coroutines library." ]
        }, {
          "title" : "Kotlin 101: Coroutines Quickly Explained | Rock the JVM",
          "url" : "https://rockthejvm.com/articles/kotlin-101-coroutines",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "Kotlin’s coroutines fall under the umbrella of structured concurrency. <strong>They implement a model of concurrency which you can consider similar to Java virtual threads, Cats Effect and ZIO fibers</strong>.",
          "page_age" : "2023-01-03T00:00:00",
          "profile" : {
            "name" : "Rock the JVM",
            "url" : "https://rockthejvm.com/articles/kotlin-101-coroutines",
            "long_name" : "rockthejvm.com",
            "img" : "https://imgs.search.brave.com/Dd2MxRsKOW6lSAUyc15emJSYF_HLaxIcoNkVywDNuQo/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOThmMTk0YTYy/MjcyM2IxNmNlZWU1/NzliMzE2ZjI3ODhj/NjI4MmEzOWQ1NmNm/ODAwYTg3N2RmOWZh/ZDI3YjE2MC9yb2Nr/dGhlanZtLmNvbS8"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "generic",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "rockthejvm.com",
            "hostname" : "rockthejvm.com",
            "favicon" : "https://imgs.search.brave.com/Dd2MxRsKOW6lSAUyc15emJSYF_HLaxIcoNkVywDNuQo/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOThmMTk0YTYy/MjcyM2IxNmNlZWU1/NzliMzE2ZjI3ODhj/NjI4MmEzOWQ1NmNm/ODAwYTg3N2RmOWZh/ZDI3YjE2MC9yb2Nr/dGhlanZtLmNvbS8",
            "path" : "› articles  › kotlin-101-coroutines"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/zZqYkroCimg-Cp2y6S3m5iEE7lXQL8QZD0g6snjXZnI/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9vZy5y/b2NrdGhlanZtLmNv/bS8_YXV0aG9ySW1h/Z2U9aHR0cHMlM0El/MkYlMkZyb2NrdGhl/anZtLmNvbSUyRl9h/c3RybyUyRnBob3Rv/LmhraGRHX0dLLnBu/ZyZhdXRob3JOYW1l/PVJpY2NhcmRvJTIw/Q2FyZGluJmxvZ289/aHR0cHMlM0ElMkYl/MkZyb2NrdGhlanZt/LmNvbSUyRmxvZ29z/JTJGcnRqdm0ucG5n/Jm1ldGFkYXRhMT0z/MSUyMG1pbiUyMHJl/YWQmbWV0YWRhdGEy/PUphbiUyMDMlMkMl/MjAyMDIzJm1ldGFk/YXRhMz1HdWlkZSZ0/YWcxPWNvbmN1cnJl/bmN5JnRhZzI9Y29y/b3V0aW5lcyZ0YWcz/PWtvdGxpbiZ0aXRs/ZT1Lb3RsaW4lMjAx/MDElM0ElMjBDb3Jv/dXRpbmVzJTIwUXVp/Y2tseSUyMEV4cGxh/aW5lZCZmb250RmFt/aWx5PVJvYm90byZm/b250VmFyaWFudD1y/ZWd1bGFyJmxheW91/dD1hcnRpY2xlJmxh/eW91dEluZGV4PTEm/Y29uZmlndXJhdGlv/bj1vZw",
            "original" : "https://og.rockthejvm.com/?authorImage=https%3A%2F%2Frockthejvm.com%2F_astro%2Fphoto.hkhdG_GK.png&authorName=Riccardo%20Cardin&logo=https%3A%2F%2Frockthejvm.com%2Flogos%2Frtjvm.png&metadata1=31%20min%20read&metadata2=Jan%203%2C%202023&metadata3=Guide&tag1=concurrency&tag2=coroutines&tag3=kotlin&title=Kotlin%20101%3A%20Coroutines%20Quickly%20Explained&fontFamily=Roboto&fontVariant=regular&layout=article&layoutIndex=1&configuration=og",
            "logo" : true
          },
          "age" : "January 3, 2023",
          "extra_snippets" : [ "All the examples we’ll present requires at least version 1.7.20 of the Kotlin compiler and version 1.6.4 of the Kotlin Coroutines library. The basic building blocks of coroutines are available in the standard library. The full implementation of the structured concurrency model is in an extension library called kotlinx-coroutines-core.", "The coroutines scheduling model is very different from the one adopted by Java Threads, called preemptive scheduling. In preemptive scheduling, the operating system decides when to switch from one thread to another. In cooperative scheduling, the coroutine itself decides when to yield the control to another coroutine. In the case of Kotlin, a coroutine decides to yield the control reaching a suspending function.", "As we can imagine, Kotlin allows us to cancel the execution of coroutines. The library provides a mechanism to cancel a coroutine cooperatively to avoid problems. The Job type provides a cancel function that cancels the execution of the coroutine. However, the cancellation is not immediate and happens only when the coroutine reaches a suspending point. The mechanism is very close to the one we saw for cooperative scheduling. Let’s see an example. We want to model that we receive an important call during the working routine.", "Discover Kotlin coroutines: a powerful tool for asynchronous programming within structured concurrency, and learn about their key features and strengths in this tutorial" ]
        }, {
          "title" : "Mastering Kotlin Coroutines with Practical Examples | by Hiten Pratap Singh | hprog99 | Medium",
          "url" : "https://medium.com/hprog99/mastering-kotlin-coroutines-with-practical-examples-1544e0bdbd64",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "It’s a lightweight thread that doesn’t require the overhead of context switching. In the world of Kotlin, a coroutine is <strong>a piece of code that can be suspended and resumed without blocking the executing thread</strong>.",
          "page_age" : "2024-05-13T08:39:31",
          "profile" : {
            "name" : "Medium",
            "url" : "https://medium.com/hprog99/mastering-kotlin-coroutines-with-practical-examples-1544e0bdbd64",
            "long_name" : "medium.com",
            "img" : "https://imgs.search.brave.com/4R4hFITz_F_be0roUiWbTZKhsywr3fnLTMTkFL5HFow/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTZhYmQ1N2Q4/NDg4ZDcyODIyMDZi/MzFmOWNhNjE3Y2E4/Y2YzMThjNjljNDIx/ZjllZmNhYTcwODhl/YTcwNDEzYy9tZWRp/dW0uY29tLw"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "article",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "medium.com",
            "hostname" : "medium.com",
            "favicon" : "https://imgs.search.brave.com/4R4hFITz_F_be0roUiWbTZKhsywr3fnLTMTkFL5HFow/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvOTZhYmQ1N2Q4/NDg4ZDcyODIyMDZi/MzFmOWNhNjE3Y2E4/Y2YzMThjNjljNDIx/ZjllZmNhYTcwODhl/YTcwNDEzYy9tZWRp/dW0uY29tLw",
            "path" : "› hprog99  › mastering-kotlin-coroutines-with-practical-examples-1544e0bdbd64"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/hDXji58gbw91okArlBX5xltKQaPWJ855Buuz29nsFq8/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9taXJv/Lm1lZGl1bS5jb20v/MSpvNWZqbEFKdmI3/U0VhaWxXNjBscFpB/LnBuZw",
            "original" : "https://miro.medium.com/1*o5fjlAJvb7SEailW60lpZA.png",
            "logo" : false
          },
          "age" : "May 13, 2024",
          "extra_snippets" : [ "Mastering Kotlin Coroutines with Practical Examples Before we dive into examples, it’s crucial to grasp the concept of coroutines. In the simplest terms, a coroutine is a way to write asynchronous …", "In the simplest terms, a coroutine is a way to write asynchronous code in a sequential manner. It’s a lightweight thread that doesn’t require the overhead of context switching. In the world of Kotlin, a coroutine is a piece of code that can be suspended and resumed without blocking the executing thread.", "Don’t catch CancellationException: If a coroutine is cancelled, it throws a CancellationException. This is a normal operation and should not be treated as an error, so you usually shouldn't catch it. Structured concurrency is one of the main benefits of using Kotlin Coroutines.", "The coroutineScope blocks the current coroutine until all of its child coroutines are completed. So, the message \"Coroutine scope is over\" is printed only after the nested launch completes its execution. Suspending functions are a cornerstone of Kotlin Coroutines." ]
        }, {
          "title" : "Coroutines | Kotlin Documentation",
          "url" : "https://kotlinlang.org/docs/coroutines-overview.html",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "To support efficient concurrency, Kotlin uses asynchronous programming built around coroutines, which <strong>let you write asynchronous code in a natural, sequential style using suspending functions</strong>. Coroutines are lightweight alternatives to threads.",
          "profile" : {
            "name" : "Kotlin",
            "url" : "https://kotlinlang.org/docs/coroutines-overview.html",
            "long_name" : "kotlinlang.org",
            "img" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "generic",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "kotlinlang.org",
            "hostname" : "kotlinlang.org",
            "favicon" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8",
            "path" : "› docs  › coroutines-overview.html"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/DlHhvc3n4mfIQ9r_tKg4tbLoTBnKanEU48c5-vzzmVg/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9rb3Rs/aW5sYW5nLm9yZy9h/c3NldHMvaW1hZ2Vz/L29wZW4tZ3JhcGgv/ZG9jcy5wbmc",
            "original" : "https://kotlinlang.org/assets/images/open-graph/docs.png",
            "logo" : false
          },
          "extra_snippets" : [ "This guide introduces the key concepts of suspending functions, coroutine builders, and structured concurrency through simple examples: Check out the KotlinConf app for a sample project to see how coroutines are used in practice.", "These, along with other possible elements, make up the coroutine context, which is inherited by default from the coroutine's parent. This context forms a hierarchy that enables structured concurrency, where related coroutines can be canceled together or handle exceptions as a group. Kotlin provides several ways for coroutines to communicate.", "Learn how to debug coroutines using built-in tools in IntelliJ IDEA. For flow-specific debugging, see the Debug Kotlin Flow using IntelliJ IDEA tutorial.", "Review best practices for using coroutines in Android. Check out the kotlinx.coroutines API reference." ]
        }, {
          "title" : "Kotlin coroutines on Android | Android Developers",
          "url" : "https://developer.android.com/kotlin/coroutines",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "A coroutine is <strong>a concurrency design pattern that you can use on Android to simplify code that executes asynchronously</strong>. Coroutines were added to Kotlin in version 1.3 and are based on established concepts from other languages.",
          "profile" : {
            "name" : "Android Developers",
            "url" : "https://developer.android.com/kotlin/coroutines",
            "long_name" : "developer.android.com",
            "img" : "https://imgs.search.brave.com/VUfGosDH1JQOAOBj9QFyCSX1qnyt7VF0qDuYkohmecU/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvZDA1ZDExMTZj/MjI4Y2NjNDdlOTRm/ZDM4ODYxYTkxMjVm/MTY5NzIwNzZkOTVh/ZTFkYTE0Zjk1NzQx/OWMwNGMyZi9kZXZl/bG9wZXIuYW5kcm9p/ZC5jb20v"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "article",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "developer.android.com",
            "hostname" : "developer.android.com",
            "favicon" : "https://imgs.search.brave.com/VUfGosDH1JQOAOBj9QFyCSX1qnyt7VF0qDuYkohmecU/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvZDA1ZDExMTZj/MjI4Y2NjNDdlOTRm/ZDM4ODYxYTkxMjVm/MTY5NzIwNzZkOTVh/ZTFkYTE0Zjk1NzQx/OWMwNGMyZi9kZXZl/bG9wZXIuYW5kcm9p/ZC5jb20v",
            "path" : "  › get started  › kotlin  › kotlin coroutines on android"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/2nBNEclULb54RmLxUE5jV3uTUvkLA0xRDBngyNRu7sQ/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9kZXZl/bG9wZXIuYW5kcm9p/ZC5jb20vc3RhdGlj/L2ltYWdlcy9zb2Np/YWwvYW5kcm9pZC1k/ZXZlbG9wZXJzLnBu/Zw",
            "original" : "https://developer.android.com/static/images/social/android-developers.png",
            "logo" : false
          },
          "extra_snippets" : [ "On Android, coroutines help to manage long-running tasks that might otherwise block the main thread and cause your app to become unresponsive. Over 50% of professional developers who use coroutines have reported seeing increased productivity. This topic describes how you can use Kotlin coroutines to address these problems, enabling you to write cleaner and more concise app code.", "For a more detailed look at coroutines on Android, see Improve app performance with Kotlin coroutines.", "Specifically, the ViewModel Architecture component calls the repository layer on the main thread to trigger the network request. This guide iterates through various solutions that use coroutines to keep the main thread unblocked.", "With the previous code, LoginViewModel is blocking the UI thread when making the network request. The simplest solution to move the execution off the main thread is to create a new coroutine and execute the network request on an I/O thread:" ]
        }, {
          "title" : "Introduction to Kotlin Coroutines | Baeldung on Kotlin",
          "url" : "https://www.baeldung.com/kotlin/coroutines",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "Simply put, coroutines allow us to create asynchronous programs in a fluent way, and they’re based on the concept of Continuation-passing style programming. The Kotlin language gives us basic constructs but can get access to more useful coroutines with the kotlinx-coroutines-core library.",
          "page_age" : "2022-07-05T10:51:31",
          "profile" : {
            "name" : "Baeldung",
            "url" : "https://www.baeldung.com/kotlin/coroutines",
            "long_name" : "baeldung.com",
            "img" : "https://imgs.search.brave.com/OI0KuMa4tikKjgqJnwNw9LDzDUOjBnzBlMsQNg1Jsig/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvMmRjN2RlZTBm/YjI1Yjg0NjFhMzkz/ZDU0MTk2NTdjNWIz/NzA4MmIxNDI4YjAx/ZDA1YTJjOTk5MDRm/NjJkNjFkYS93d3cu/YmFlbGR1bmcuY29t/Lw"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "article",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "baeldung.com",
            "hostname" : "www.baeldung.com",
            "favicon" : "https://imgs.search.brave.com/OI0KuMa4tikKjgqJnwNw9LDzDUOjBnzBlMsQNg1Jsig/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvMmRjN2RlZTBm/YjI1Yjg0NjFhMzkz/ZDU0MTk2NTdjNWIz/NzA4MmIxNDI4YjAx/ZDA1YTJjOTk5MDRm/NjJkNjFkYS93d3cu/YmFlbGR1bmcuY29t/Lw",
            "path" : "  › home  › asynchronous programming  › introduction to kotlin coroutines"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/LX9ZL-cdIyIBTbxIysAmIKOn5z12Tj6Dj34XgtmDVhY/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly93d3cu/YmFlbGR1bmcuY29t/L3dwLWNvbnRlbnQv/dXBsb2Fkcy9zaXRl/cy81LzIwMjAvMDEv/S290bGluLTItT24t/QmFlbGR1bmcucG5n",
            "original" : "https://www.baeldung.com/wp-content/uploads/sites/5/2020/01/Kotlin-2-On-Baeldung.png",
            "logo" : false
          },
          "age" : "July 5, 2022",
          "extra_snippets" : [ "Simply put, coroutines allow us to create asynchronous programs in a fluent way, and they’re based on the concept of Continuation-passing style programming. The Kotlin language gives us basic constructs but can get access to more useful coroutines with the kotlinx-coroutines-core library.", "We’ll be looking at this library once we understand the basic building blocks of the Kotlin language. Let’s create the first coroutine using the buildSequence function.", "The suspend keyword means that this function can be blocking. Such a function can suspend a buildSequence coroutine. Suspending functions can be created as standard Kotlin functions, but we need to be aware that we can only call them from within a coroutine.", "We saw that sequence is the main building block of every coroutine. We described how the flow of execution in this Continuation-passing programming style looks. Finally, we looked at the kotlinx-coroutines library that ships a lot of very useful constructs for creating asynchronous programs." ]
        }, {
          "title" : "Coroutines guide | Kotlin Documentation",
          "url" : "https://kotlinlang.org/docs/coroutines-guide.html",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "It contains a number of high-level coroutine-enabled primitives that this guide covers, including launch, async, and others. This is a guide about the core features of kotlinx.coroutines with a series of examples, divided up into different topics.",
          "profile" : {
            "name" : "Kotlin",
            "url" : "https://kotlinlang.org/docs/coroutines-guide.html",
            "long_name" : "kotlinlang.org",
            "img" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "generic",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "kotlinlang.org",
            "hostname" : "kotlinlang.org",
            "favicon" : "https://imgs.search.brave.com/w-33dTy8EmKFlZidM_8nSQaPi2fufqlYJ9d0zzysFgI/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYmRkOGFlZGRh/MDE3OWNmNzMzMDhl/OTIwYThlNjJjMGFj/MWIyZGZmZDQ3MmQ0/YjUxMjhhYmQyMDhi/YmQ1MzMyYi9rb3Rs/aW5sYW5nLm9yZy8",
            "path" : "› docs  › coroutines-guide.html"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/DlHhvc3n4mfIQ9r_tKg4tbLoTBnKanEU48c5-vzzmVg/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9rb3Rs/aW5sYW5nLm9yZy9h/c3NldHMvaW1hZ2Vz/L29wZW4tZ3JhcGgv/ZG9jcy5wbmc",
            "original" : "https://kotlinlang.org/assets/images/open-graph/docs.png",
            "logo" : false
          },
          "extra_snippets" : [ "Kotlin provides only minimal low-level APIs in its standard library to enable other libraries to utilize coroutines. Unlike many other languages with similar capabilities, async and await are not keywords in Kotlin and are not even part of its standard library.", "It contains a number of high-level coroutine-enabled primitives that this guide covers, including launch, async, and others. This is a guide about the core features of kotlinx.coroutines with a series of examples, divided up into different topics.", "kotlinx.coroutines is a rich library for coroutines developed by JetBrains." ]
        }, {
          "title" : "Understanding Kotlin Coroutines",
          "url" : "https://reflectoring.io/understanding-kotlin-coroutines-tutorial/",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "Coroutines are <strong>a design pattern for writing asynchronous programs for running multiple tasks concurrently</strong>. In asynchronous programs, multiple tasks execute in parallel on separate threads without waiting for the other tasks to complete.",
          "page_age" : "2022-07-14T05:00:00",
          "profile" : {
            "name" : "Reflectoring",
            "url" : "https://reflectoring.io/understanding-kotlin-coroutines-tutorial/",
            "long_name" : "reflectoring.io",
            "img" : "https://imgs.search.brave.com/_pM1b5vIemZw3wCYQFnPFQ6VjXzvjPyTv57BBixklwg/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvNzg5MzVjZjBl/ZTYzOTU5MzcwZDQ1/MGE5MGEwNTYwZWNh/ZjE0MjU2OTMxZDUx/OGY4Y2M2MjQ4NWE4/Zjk4NDRkNy9yZWZs/ZWN0b3JpbmcuaW8v"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "generic",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "reflectoring.io",
            "hostname" : "reflectoring.io",
            "favicon" : "https://imgs.search.brave.com/_pM1b5vIemZw3wCYQFnPFQ6VjXzvjPyTv57BBixklwg/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvNzg5MzVjZjBl/ZTYzOTU5MzcwZDQ1/MGE5MGEwNTYwZWNh/ZjE0MjU2OTMxZDUx/OGY4Y2M2MjQ4NWE4/Zjk4NDRkNy9yZWZs/ZWN0b3JpbmcuaW8v",
            "path" : "› understanding-kotlin-coroutines-tutorial"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/8NPxdh_s0RWND7_KNv5GPX6gpWeGFEzrlaBlOiSOIbI/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9yZWZs/ZWN0b3JpbmcuaW8v/aW1hZ2VzL3N0b2Nr/LzAwNTQtYmVlLTEy/MDB4NjI4LWJyYW5k/ZWQuanBn",
            "original" : "https://reflectoring.io/images/stock/0054-bee-1200x628-branded.jpg",
            "logo" : false
          },
          "age" : "July 14, 2022",
          "extra_snippets" : [ "We will change this program to run using coroutines in the next sections. The Kotlin language gives us basic constructs for writing coroutines but more useful constructs built on top of the basic coroutines are available in the kotlinx-coroutines-core library.", "Let us run this program to observe how the coroutine suspends and allows the thread to run the other regular functions: 2022-06-24T04:09:40..: My program runs...: main 2022-06-24T04:09:40..: findProduct on...: main 2022-06-24T04:09:40..: fetchPrice starts on...: main 2022-06-24T04:09:40..: updateProduct on...: main 2022-06-24T04:09:40..: My program run ends...: main 2022-06-24T04:09:42..: fetchPrice ends on.: kotlinx.coroutines.DefaultExecutor Process finished with exit code 0", "The CoroutineContext is used to explicitly specify the dispatcher for the new coroutine. Kotlin has multiple implementations of CoroutineDispatchers which we can specify when creating coroutines with coroutine builders like launch and async.", "In this article, we understood the different ways of using Coroutines in Kotlin. Here are some important points to remember: A coroutine is a concurrency design pattern used to write asynchronous programs." ]
        }, {
          "title" : "Understanding Kotlin Coroutines with this mental model | Lukas Lechner",
          "url" : "https://www.lukaslechner.com/understanding-kotlin-coroutines-with-this-mental-model/",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "In order to be able to successfully develop applications with coroutines, it is not necessary, to know all the source code in the coroutines library or the exact functioning of the compiler, however, a basic mental model about the coroutines machinery is essential to understand how code in coroutines is executed in contrast to regular code. Let’s start our journey with an analysis of the term itself: It consists of CO and ROUTINE. Every developer is familiar with ordinary routines. They are also called subroutines or procedures, but in Java and Kotlin they are known as functions or methods.",
          "page_age" : "2022-11-21T11:31:26",
          "profile" : {
            "name" : "Lukas Lechner",
            "url" : "https://www.lukaslechner.com/understanding-kotlin-coroutines-with-this-mental-model/",
            "long_name" : "lukaslechner.com",
            "img" : "https://imgs.search.brave.com/Kn9n6W89bVvugTrDrwyDDnWyHAvSruZEl3vqHGZtdSQ/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYWVjNzM1YTk5/YWVlOTBjYThiZDdi/YjJkMjc2NWY5NjZh/ZWVjZTNhOWE4OWY5/ZDhiYjRlMzIyMDFh/Y2EyYTM3Yy93d3cu/bHVrYXNsZWNobmVy/LmNvbS8"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "article",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "lukaslechner.com",
            "hostname" : "www.lukaslechner.com",
            "favicon" : "https://imgs.search.brave.com/Kn9n6W89bVvugTrDrwyDDnWyHAvSruZEl3vqHGZtdSQ/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvYWVjNzM1YTk5/YWVlOTBjYThiZDdi/YjJkMjc2NWY5NjZh/ZWVjZTNhOWE4OWY5/ZDhiYjRlMzIyMDFh/Y2EyYTM3Yy93d3cu/bHVrYXNsZWNobmVy/LmNvbS8",
            "path" : "  › home  › understanding kotlin coroutines with this mental model"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/uRFMF0BlabOkVgvkc4DNfw-tFfaiVwxL4ZTnkvzOFUM/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly9pMC53/cC5jb20vd3d3Lmx1/a2FzbGVjaG5lci5j/b20vd3AtY29udGVu/dC91cGxvYWRzLzIw/MjAvMDUvaGVhZGVy/aW1hZ2UtMy5wbmc_/Zml0PTEwMjQlMkM1/MTImc3NsPTE",
            "original" : "https://i0.wp.com/www.lukaslechner.com/wp-content/uploads/2020/05/headerimage-3.png?fit=1024%2C512&ssl=1"
          },
          "age" : "November 21, 2022",
          "extra_snippets" : [ "In order to be able to successfully develop applications with coroutines, it is not necessary, to know all the source code in the coroutines library or the exact functioning of the compiler, however, a basic mental model about the coroutines machinery is essential to understand how code in coroutines is executed in contrast to regular code. Let’s start our journey with an analysis of the term itself: It consists of CO and ROUTINE. Every developer is familiar with ordinary routines. They are also called subroutines or procedures, but in Java and Kotlin they are known as functions or methods.", "In the real world, when requirements are often more complex, their insufficient understanding of coroutines fails them to develop efficient and correct concurrent software. This blog post will help you to form a solid mental model about this new emerging concept for modern software development.", "ℹ️ You can also check out my new open source project, in which I developed 16 sample implementations of the most common use cases of Kotlin Coroutines for Android Development. I think you will find it helpful. Mental models are how we understand the world.", "It’s difficult to understand how it is possible that coroutines allow us to write concurrent code on a single thread. You might think that Jetbrains uses some kind of black JVM magic to make them work. But in fact, everything becomes clear as soon as you understand what the Kotlin compiler is doing when coroutine-based coded is compiled to byte code." ]
        }, {
          "title" : "Understanding Kotlin Coroutines with examples | DECODE",
          "url" : "https://decode.agency/article/understanding-kotlin-coroutines/",
          "is_source_local" : false,
          "is_source_both" : false,
          "description" : "Coroutines are <strong>a Kotlin library for handling async tasks</strong>. They make asynchronous code easier to write and read. The functionality that coroutines provide is the ability to suspend a coroutine at some point and resume it in the future.",
          "page_age" : "2022-12-12T17:33:58",
          "profile" : {
            "name" : "DECODE",
            "url" : "https://decode.agency/article/understanding-kotlin-coroutines/",
            "long_name" : "decode.agency",
            "img" : "https://imgs.search.brave.com/Seru0JljU4u3Q5Ygeg4c9p6X6rBe616u1y0AaJ9-R3k/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvNGYyMGMyNWMx/NDc2NjEzY2U1MGI5/MjI0NDdmNjEzOTdl/NTEzYWJjNmNiZDIy/ZDk0ZWYyYjk1MTg3/MmRlNzgwNS9kZWNv/ZGUuYWdlbmN5Lw"
          },
          "language" : "en",
          "family_friendly" : true,
          "type" : "search_result",
          "subtype" : "article",
          "is_live" : false,
          "meta_url" : {
            "scheme" : "https",
            "netloc" : "decode.agency",
            "hostname" : "decode.agency",
            "favicon" : "https://imgs.search.brave.com/Seru0JljU4u3Q5Ygeg4c9p6X6rBe616u1y0AaJ9-R3k/rs:fit:32:32:1:0/g:ce/aHR0cDovL2Zhdmlj/b25zLnNlYXJjaC5i/cmF2ZS5jb20vaWNv/bnMvNGYyMGMyNWMx/NDc2NjEzY2U1MGI5/MjI0NDdmNjEzOTdl/NTEzYWJjNmNiZDIy/ZDk0ZWYyYjk1MTg3/MmRlNzgwNS9kZWNv/ZGUuYWdlbmN5Lw",
            "path" : "› article  › understanding-kotlin-coroutines"
          },
          "thumbnail" : {
            "src" : "https://imgs.search.brave.com/xAxVLXd9Iac9-phb8YlPna9v8SuMObUrzWeX4nnuJ9U/rs:fit:200:200:1:0/g:ce/aHR0cHM6Ly8xMzIy/MDI0MzQxLnJzYy5j/ZG43Ny5vcmcvd3At/Y29udGVudC91cGxv/YWRzLzIwMjIvMDYv/TW9iaWxlLWFwcC1k/ZXNpZ24tYmVzdC1w/cmFjdGljZXMtdG8t/a2VlcC1pbi1taW5k/LTEucG5n",
            "original" : "https://1322024341.rsc.cdn77.org/wp-content/uploads/2022/06/Mobile-app-design-best-practices-to-keep-in-mind-1.png",
            "logo" : false
          },
          "age" : "December 12, 2022",
          "extra_snippets" : [ "Coroutines are a Kotlin library for handling async tasks. They make asynchronous code easier to write and read. The functionality that coroutines provide is the ability to suspend a coroutine at some point and resume it in the future. Thanks to that functionality, we can run our code on the Main thread and suspend it when we request data from an API. When a coroutine is suspended, the thread is not blocked (this is called “suspension” and will be explained below) and is free to be used for other functionality for example, showing a progress bar indicator.", "Everything you need to know about Kotlin Coroutines with code examples.", "Now let’s talk about the main feature of Coroutines. The suspension capability is the most essential feature upon which all Kotlin coroutines are built.", "Now that we know the basic functionality of what suspension is and how it works, let’s explore some additional coroutine features and see how they are actually created and used. The first section covered the basics of suspending functions, what suspension actually is, and how it works. This simple functionality is achievable by using built-in support in the Kotlin programming language." ]
        } ],
        "family_friendly" : true
      }
    }
""".trimIndent()