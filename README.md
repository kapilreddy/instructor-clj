# instructor-clj

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.kapil/instructor-clj.svg)](https://clojars.org/org.clojars.kapil/instructor-clj)
[![cljdoc](https://cljdoc.org/badge/org.clojars.kapil/instructor-clj)](https://cljdoc.org/d/org.clojars.kapil/instructor-clj)
[![Test](https://github.com/kapilreddy/instructor-clj/actions/workflows/test.yml/badge.svg)](https://github.com/kapilreddy/instructor-clj/actions/workflows/test.yml)
[![Lint](https://github.com/kapilreddy/instructor-clj/actions/workflows/lint.yml/badge.svg)](https://github.com/kapilreddy/instructor-clj/actions/workflows/lint.yml)

instructor-clj is a Clojure lib inspired by [instructor](https://github.com/jxnl/instructor)
It makes it easy to have structured output from LLMs.

Built on top of [Malli](https://github.com/metosin/malli) for defining schemas and [litellm-clj](https://github.com/unravel-team/clj-litellm) for LLM provider support.

## Features

- 🎯 Structured output from LLMs using Malli schemas
- 🔄 Automatic retry mechanism for failed requests
- 🌐 Multi-provider support (OpenAI, Anthropic, Gemini, Mistral, Ollama, OpenRouter)
- ✅ Built-in validation and parsing
- 📦 Simple API with minimal configuration

## Examples

```clojure
(require '[instructor-clj.core :as ic])

(def User
  [:map
   [:name :string]
   [:age :int]])

(ic/instruct "Kapil Reddy is almost 40 years old."
             User
             :api-key "<API-KEY>"
             :provider :openai)
; => {:name "John Doe", :age 30}
```

```clojure
(def Meeting
  [:map
   [:action [:and {:description "What action is needed"}
             [:enum "call" "followup"]]]
   [:person [:and {:description "Person involved in the action"}
             [:string]]]
   [:time [:and {:description "Time of the day"}
           [:string]]]
   [:day [:and {:description "Day of the week"}
          [:string]]]])

; With retries:
(ic/instruct "Call Kapil on Saturday at 12pm"
             Meeting
             :api-key api-key
             :provider :openai
             :model "gpt-4"
             :max-retries 2)
; => {:action "call", :person "Kapil", :time "12pm", :day "Saturday"}
```

Using `create-chat-completion` for more control:

```clojure
(ic/create-chat-completion
 {:messages [{:role :user :content "Call Kapil on Saturday at 12pm"}]
  :model "gpt-3.5-turbo"
  :provider :openai
  :response-model Meeting
  :api-key api-key})
; => {:action "call", :person "Kapil", :time "12pm", :day "Saturday"}
```

With additional parameters:

```clojure
(ic/create-chat-completion
 {:messages [{:role :user :content "Call Kapil on Saturday at 12pm"}]
  :model "gpt-4"
  :temperature 0.5
  :max-tokens 1000
  :provider :openai
  :response-model Meeting
  :api-key api-key})
```

## Installation

Add to your `deps.edn`:

```clojure
{:deps {org.clojars.kapil/instructor-clj {:mvn/version "0.0.1-alpha.3"}}}
```

## Development

This project uses [deps.edn](https://clojure.org/guides/deps_and_cli) for dependency management.

### Running Tests

#### Unit Tests Only
```bash
clojure -M:test -m cognitect.test-runner
```

#### Integration Tests (requires OPENAI_API_KEY)
```bash
export OPENAI_API_KEY=your-api-key
clojure -M:test -m cognitect.test-runner
```

### Building

```bash
# Build JAR
clojure -T:build jar

# Install locally
clojure -T:build install

# Deploy to Clojars
clojure -T:build deploy
```

## API Reference

### `instruct`

Simple API for structured output from a prompt.

```clojure
(instruct prompt schema & {:keys [api-key model temperature max-retries provider]})
```

### `create-chat-completion`

More explicit API with full control over messages and parameters.

```clojure
(create-chat-completion {:messages [...] 
                         :model "gpt-3.5-turbo"
                         :response-model schema
                         :api-key "..."
                         :provider :openai})
```

**Note:** Message roles can be either strings or keywords (`:user`, `:assistant`, `:system`, `:tool`).

## Multi-Provider Support

instructor-clj supports multiple LLM providers through [litellm-clj](https://github.com/unravel-team/clj-litellm):

- **OpenAI**: `gpt-3.5-turbo`, `gpt-4`, `gpt-4o`, etc.
- **Anthropic**: `claude-3-opus-20240229`, `claude-3-sonnet-20240229`, etc.
- **Google Gemini**: `gemini-pro`, `gemini-1.5-pro`, etc.
- **Mistral**: `mistral-medium`, `mistral-large`, etc.
- **Ollama**: `llama3`, `mixtral`, `phi`, etc.
- **OpenRouter**: Any model in format `provider/model`

Set appropriate API keys via environment variables:
- `OPENAI_API_KEY` for OpenAI
- `ANTHROPIC_API_KEY` for Anthropic
- `GEMINI_API_KEY` for Google Gemini
- `OPENROUTER_API_KEY` for OpenRouter

## License

This project is licensed under the terms of the MIT License.
