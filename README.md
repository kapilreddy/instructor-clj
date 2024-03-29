# instructor-clj

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.kapil/instructor-clj.svg)](https://clojars.org/org.clojars.kapil/instructor-clj)

instructor-clj is a Clojure lib inspired by [instructor](https://github.com/jxnl/instructor)
It makes it easy to have structured output from LLMs.

Built on top of [Malli](https://github.com/metosin/malli) for defining schemas.

## Examples

```clojure
(require '[instructor-clj.core :as ic])

(def User
  [:map
   [:name :string]
   [:age :int]])

(instruct "John Doe is 30 years old."
          User
          :api-key "<API-KEY>")
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
(instruct "Call Kapil on Saturday at 12pm"
          Meeting
          :api-key api-key
          :model "gpt-4"
          :max-retries 2)
; => {:action "call", :person "Kapil", :time "12pm", :day "Saturday"}
```

## License

This project is licensed under the terms of the MIT License.
