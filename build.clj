(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'org.clojars.kapil/instructor-clj)
(def version "1.0.0-alpha")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]
                :pom-data [[:description "A Clojure implementation of Python library https://github.com/jxnl/instructor"]
                           [:url "https://github.com/kapilreddy/instructor-clj"]
                           [:licenses
                            [:license
                             [:name "The MIT License"]
                             [:url "http://opensource.org/licenses/MIT"]]]
                           [:developers
                            [:developer
                             [:name "Kapil Reddy"]]]
                           [:scm
                            [:url "https://github.com/kapilreddy/instructor-clj"]
                            [:connection "scm:git:git://github.com/kapilreddy/instructor-clj.git"]
                            [:developerConnection "scm:git:ssh://git@github.com/kapilreddy/instructor-clj.git"]
                            [:tag version]]]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn install [_]
  (jar nil)
  (b/install {:basis basis
              :lib lib
              :version version
              :jar-file jar-file
              :class-dir class-dir}))

(defn deploy [opts]
  (jar nil)
  ((requiring-resolve 'deps-deploy.deps-deploy/deploy)
   (merge {:installer :remote
           :artifact jar-file
           :pom-file (b/pom-path {:lib lib :class-dir class-dir})}
          opts)))
