# instructor-clj
instructor-clj is a Clojure lib inspired by [instructor](https://github.com/jxnl/instructor)
It makes it easy to have structured output from LLMs.

Built on top of [Malli](https://github.com/metosin/malli) for defining schemas.

# Examples

```clojure
(require '[instructor-clj.core :as ic])

(def User
  [:map
   [:name :string]
   [:age :int]])

(ic/llm->response "John Doe is 30 years old."
                  User
                  :api-key "<API-KEY>")
; => {:name "John Doe", :age 30}
```


## License
This project is licensed under the terms of the MIT License.
