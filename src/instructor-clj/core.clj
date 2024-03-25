(ns instructor-clj.core
  (:require [org.httpkit.client :as http]
            [stencil.core :as sc]
            [malli.core :as m]
            [malli.json-schema :as json-schema]
            [cheshire.core :as cc])
  (:import [com.fasterxml.jackson.core JsonParseException]))


(defn schema->system-prompt
  "Convert a malli schema into JSON schema and generate a system prompt for responses"
  [schema]
  (sc/render-string "As a genius expert, your task is to understand the content and provide
                the parsed objects in json that match the following json_schema:\n

                {{schema}}

                Make sure to return an instance of the JSON, not the schema itselfHi there, {{name}}."
                    {:schema (json-schema/transform schema)}))


(defn llm->response
  "response-schema - Valid malli schema

  The function tries to destructure and get the actual response.
  @TODO Add ability to plugin different LLMs
  @TODO Getting response is brittle and not extensible for different LLMs"
  [prompt response-schema & {:keys [api-key]}]
  (let [api-url "https://api.openai.com/v1/chat/completions"
        headers {"Authorization" (str "Bearer " api-key)
                 "Content-Type" "application/json"}
        body (cc/generate-string {"model" "gpt-3.5-turbo"
                                  "messages"  [{"role" "system"
                                                "content" (schema->system-prompt response-schema)}
                                               {"role" "user"
                                                "content" prompt}]
                                  "temperature" 0.7
                                  "max_tokens" 150})
        response (try
                   (-> (http/post api-url {:headers headers
                                           :body body})
                       deref ;; Dereference the future
                       :body
                       (cc/parse-string true)
                       :choices
                       first
                       :message
                       :content
                       (cc/parse-string true))
                   (catch JsonParseException _
                     ))]
    (when (m/validate response-schema response)
      response)))

;; Example usage
(comment

    ;; https://github.com/jxnl/instructor/blob/cea534fd2280371d2778e0f043d3fe557cc7bc7e/instructor/process_response.py#L245C17-L250C83

  (def User
    [:map
     [:name :string]
     [:age :int]])

  (llm->response "John Doe is 30 years old."
                  User
                  :api-key "<API-KEY>"))
