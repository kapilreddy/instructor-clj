(defproject org.clojars.kapil/instructor-clj "0.0.1-alpha.3"
  :author "Kapil Reddy <https://www.kapilreddy.me>"
  :description "A Clojure implementation of Python library https://github.com/jxnl/instructor"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies
  [[org.clojure/clojure "1.11.2"]
   [cheshire "5.12.0"]
   [metosin/malli "0.15.0"]
   [stencil "0.5.0"]
   [http-kit "2.7.0"]
   [circleci/bond "0.6.0"]
   [net.clojars.wkok/openai-clojure "0.16.0"]])
