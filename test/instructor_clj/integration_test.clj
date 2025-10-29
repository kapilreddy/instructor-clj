(ns instructor-clj.integration-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [instructor-clj.core :as icc]))


(def api-key (System/getenv "OPENAI_API_KEY"))


(defn check-api-key
  "Fixture to ensure API key is present before running integration tests"
  [f]
  (if api-key
    (f)
    (println "Skipping integration tests: OPENAI_API_KEY environment variable not set")))


(use-fixtures :once check-api-key)


(deftest test-instruct-integration
  (testing "instruct function with real OpenAI API"
    (when api-key
      (let [User [:map
                  [:name :string]
                  [:age :int]]
            response (icc/instruct "John Doe is 30 years old."
                                   User
                                   :api-key api-key
                                   :provider :openai
                                   :model "gpt-3.5-turbo"
                                   :max-retries 0)]
        (is (some? response) "Response should not be nil")
        (is (= "John Doe" (:name response)) "Name should be extracted correctly")
        (is (= 30 (:age response)) "Age should be extracted correctly")))))


(deftest test-create-chat-completion-integration
  (testing "create-chat-completion function with real OpenAI API"
    (when api-key
      (let [Meeting [:map
                     [:action [:and {:description "What action is needed"}
                               [:enum "call" "followup"]]]
                     [:person [:and {:description "Person involved in the action"}
                               [:string]]]
                     [:time [:and {:description "Time of the day"}
                             [:string]]]
                     [:day [:and {:description "Day of the week"}
                            [:string]]]]
            response (icc/create-chat-completion
                      {:messages [{:role "user" 
                                   :content "Call Kapil on Saturday at 12pm"}]
                       :model "gpt-3.5-turbo"
                       :provider :openai
                       :response-model Meeting
                       :api-key api-key})]
        (is (some? response) "Response should not be nil")
        (is (= "call" (:action response)) "Action should be 'call'")
        (is (= "Kapil" (:person response)) "Person should be 'Kapil'")
        (is (= "12pm" (:time response)) "Time should be '12pm'")
        (is (= "Saturday" (:day response)) "Day should be 'Saturday'")))))


(deftest test-multiple-providers-integration
  (testing "Support for multiple LLM providers"
    (when api-key
      (let [User [:map
                  [:name :string]
                  [:age :int]]]
        
        ;; Test with OpenAI with explicit provider
        (let [response (icc/instruct "Alice Smith is 25 years old."
                                     User
                                     :api-key api-key
                                     :provider :openai
                                     :model "gpt-3.5-turbo"
                                     :max-retries 0)]
          (is (some? response) "OpenAI response should not be nil")
          (is (string? (:name response)) "Name should be a string")
          (is (int? (:age response)) "Age should be an integer"))
        
        ;; Test explicit provider specification
        (let [response (icc/instruct "Bob Johnson is 35 years old."
                                     User
                                     :api-key api-key
                                     :model "gpt-3.5-turbo"
                                     :provider :openai
                                     :max-retries 0)]
          (is (some? response) "Explicit provider response should not be nil")
          (is (string? (:name response)) "Name should be a string")
          (is (int? (:age response)) "Age should be an integer"))))))


(deftest test-complex-schema-integration
  (testing "Complex nested schema extraction"
    (when api-key
      (let [Address [:map
                     [:street :string]
                     [:city :string]
                     [:country :string]]
            Person [:map
                    [:name :string]
                    [:age :int]
                    [:address Address]]
            prompt "John Doe is 30 years old and lives at 123 Main Street, New York, USA"
            response (icc/create-chat-completion
                      {:messages [{:role "user" :content prompt}]
                       :model "gpt-4o-mini"
                       :provider :openai
                       :response-model Person
                       :api-key api-key})]
        (is (some? response) "Response should not be nil")
        (is (string? (:name response)) "Name should be a string")
        (is (int? (:age response)) "Age should be an integer")
        (is (map? (:address response)) "Address should be a map")
        (is (string? (get-in response [:address :street])) "Street should be a string")
        (is (string? (get-in response [:address :city])) "City should be a string")
        (is (string? (get-in response [:address :country])) "Country should be a string")))))


(deftest test-error-handling-integration
  (testing "Proper error handling with invalid API key"
    (let [User [:map
                [:name :string]
                [:age :int]]]
      (try
        (icc/instruct "John Doe is 30 years old."
                      User
                      :api-key "invalid-api-key"
                      :provider :openai
                      :model "gpt-3.5-turbo"
                      :max-retries 0)
        (is false "Should have thrown an exception")
        (catch Exception e
          (is (some? e) "Exception should be thrown for invalid API key"))))))


(deftest test-retry-mechanism-integration
  (testing "Retry mechanism with real API"
    (when api-key
      (let [User [:map
                  [:name :string]
                  [:age :int]]
            ;; This should succeed even with retries enabled
            response (icc/instruct "Sarah Wilson is 28 years old."
                                   User
                                   :api-key api-key
                                   :provider :openai
                                   :model "gpt-3.5-turbo"
                                   :max-retries 2)]
        (is (some? response) "Response should not be nil with retries")
        (is (string? (:name response)) "Name should be extracted")
        (is (int? (:age response)) "Age should be extracted")))))
