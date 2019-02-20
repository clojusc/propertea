(defproject clojusc/propertea "1.5.0-SNAPSHOT"
  :description "Load, coerce, and validate property files."
  :dependencies [
    [org.clojure/clojure "1.10.0"]
    [expectations "2.1.10"]]
  :plugins [
    [lein-expectations "0.0.8"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.3.5"]
        [lein-ancient "0.6.15"]
        [lein-kibit "0.1.6"]]}}
  :aliases {
    ;; Dev & Testing Aliases
    "repl" ["do"
      ["clean"]
      ["repl"]]
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ["eastwood"]
      ]
    "build" ["do"
      ["clean"]
      ["check-vers"]
      ["ubercompile"]
      ["lint"]
      ["test"]
      ["jar"]]})
