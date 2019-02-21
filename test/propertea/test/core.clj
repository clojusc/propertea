(ns propertea.test.core
  (:require
    [clojure.java.io :as io]
    [expectations :refer :all]
    [propertea.core :as prop])
  (:import
    (java.util Properties)))

(def fp (io/file "test/fake.properties"))
(def instream (io/input-stream fp))
(def props (doto (Properties.)
                 (.setProperty "key-from-properties" "value-in-properties")))

;;; read a string
(expect "hello-string" (:string-example (prop/read-properties fp)))
(expect "hello-string" (:string-example (prop/read-properties instream)))

;;; read a string from properties object
(expect "value-in-properties" (:key-from-properties (prop/read-properties props)))

;;; read and convert the string into an int
(expect 1 (:int-example (prop/read-properties fp :as-int [:int-example])))

;;; read and convert an invalid int string into nil
(expect nil (:string-example (prop/read-properties fp :as-int [:string-example])))

;;; read and convert the string into a boolean
(expect true? (:boolean-example (prop/read-properties fp :as-bool [:boolean-example])))

;;; read and convert an invalid bool string into nil
(expect nil (:string-example (prop/read-properties fp :as-bool [:string-example])))

;;; add nil to the properties if attempting to int parse a non-existent value
(expect nil (:l (prop/read-properties fp :as-int [:l])))

;;; add nil to the properties if attempting to bool parse a non-existent value
(expect nil (:l (prop/read-properties fp :as-boolean [:l])))

;;; include a default value if a value doesn't exist
(expect :def-val (:l (prop/read-properties fp :default [:l :def-val])))

;;; don't include a default value if a value does exist
(expect "hello-string"
        (:string-example (prop/read-properties fp :default [:string-example :def-val])))

;;; include a default value even if parsing fails due to it not existing
(expect true? (:l (prop/read-properties fp :default [:l true] :as-bool [:l])))

;;; throw an exception if something is required and doesn't exist
(expect RuntimeException (prop/read-properties fp :required [:foo :int-example]))

;;; throw an exception if something exists but is an empty string
(expect RuntimeException (prop/read-properties fp :required [:empty-string]))

;;; throw an exception if invalid parsing occurs, resulting in nil
;;; and it is also required
(expect RuntimeException
        (prop/read-properties fp :required [:string-example] :as-int [:string-example]))

;;; show me the properties
(expect Properties (prop/map->properties {"A" 1 "B" 2}))

;;; get a value
(expect "1" (.get (prop/map->properties {"A" 1 "B" 2}) "A"))

;;; get a value after converting keywords to strings
(expect "1" (.get (prop/map->properties {:A 1 :B 2}) "A"))

;;; get a value after converting symbols to strings
(expect "1" (.get (prop/map->properties {'A 1 'B 2}) "A"))

;;; get a value after converting any object to a string
(expect "1" (.get (prop/map->properties {1 1 2 2}) "1"))

;;; nest map
(expect "5"
        (get-in
         (prop/read-properties fp :nested true)
         [:nested :example :depth]))

;;; nest map
(expect "2"
        (get-in
         (prop/read-properties fp :nested true)
         [:nested :example :leaves]))

(expect [String]
        (->> (prop/read-properties fp :stringify-keys true)
            keys
            (map class)
            distinct))

(expect "get-dashed"
        (get-in
         (prop/read-properties fp :nested true :dasherize-keys true)
         [:nested :with-camel-case]))

(expect "get-dashed"
        (get-in
         (prop/read-properties fp :nested true)
         [:nested :withCamelCase]))
