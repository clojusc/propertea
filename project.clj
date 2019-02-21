(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      ; (slurp "resources/text/loading.txt")
      )
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m =>\u001B[m "))

(defproject clojusc/propertea "1.5.0"
  :description "Load, coerce, and validate property files."
  :url ""
  :license {
    :name "BSD 3-Clause"
    :url "https://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [
    [org.clojure/clojure "1.10.0"]]
  :profiles {
    :dev {
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns propertea.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :ubercompile {
      :aot :all}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.3.5"]
        [lein-ancient "0.6.15"]
        [lein-kibit "0.1.6"]]}
    :test {
      :dependencies [
        [expectations "2.1.10"]]
      :plugins [
        [lein-expectations "0.0.8"]]}}
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
