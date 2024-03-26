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

                Make sure to return an instance of only the JSON, not the schema itself and no text explaining the JSON"
                    {:schema (json-schema/transform schema)}))


(defn llm->response
  "response-schema - Valid malli schema

  The function tries to destructure and get the actual response.
  @TODO Add ability to plugin different LLMs
  @TODO Getting response is brittle and not extensible for different LLMs"
  [prompt response-schema & {:keys [api-key max-tokens model temprature]
                             :or {max-tokens 4097
                                  temprature 0.7
                                  model "gpt-3.5-turbo"}}]
  (let [api-url "https://api.openai.com/v1/chat/completions"
        headers {"Authorization" (str "Bearer " api-key)
                 "Content-Type" "application/json"}
        body (cc/generate-string {"model" model
                                  "messages"  [{"role" "system"
                                                "content" (schema->system-prompt response-schema)}
                                               {"role" "user"
                                                "content" prompt}]
                                  "temperature" temprature
                                  "max_tokens" max-tokens})
        body (-> (http/post api-url {:headers headers
                                     :body body})
                 deref ;; Dereference the future
                 :body)
        response (try
                   (-> body
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
