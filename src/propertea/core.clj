(ns propertea.core
  (:require
    [clojure.set :as set]
    [clojure.string :as string]
    [clojure.walk :as walk])
  (:import
    (java.io FileReader BufferedInputStream)
    (java.util Properties)))

(defn keywordize-keys-unless [m b]
  (if b
    m
    (walk/keywordize-keys m)))

(defn dash-match [[ _ g1 g2]]
  (str g1 "-" g2))

(defn dasherize [k]
  (-> k
      (string/replace #"([A-Z]+)([A-Z][a-z])" dash-match)
      (string/replace #"([a-z\d])([A-Z])" dash-match)
      (string/lower-case)))

(defn properties->map [props nested kf]
  (reduce (fn [r [k v]]
            (if nested
              (assoc-in r (string/split (kf k) #"\.") v)
              (assoc r (kf k) v)))
          {}
          props))

(defn input-stream->properties [stream]
  (doto (Properties.) (.load stream)))

(defn file-name->properties [file-name]
  (doto (Properties.)
    (.load (FileReader. file-name))))

(defmulti valid? class :default :default)

(defmethod valid? String [a]
  (seq a))

(defmethod valid? nil [a]
  false)

(defmethod valid? :default [a]
  true)

(defn validate [m required-list]
  (let [ks (reduce (fn [r [k v]] (if (valid? v) (conj r k) r)) #{} m)
        rks (set required-list)]
    (if-let [not-found (seq (set/difference rks ks))]
      (throw (RuntimeException. (str not-found " are required, but not found")))
      m)))

(defn dump [m f]
  (when f
    (doseq [[k v] m]
      (f (pr-str k v))))
  m)

(defn parse-int-fn [v]
  (try
    (Integer/parseInt v)
    (catch NumberFormatException e
      nil)))

(defn parse-bool-fn [v]
  (when v
    (condp = (string/lower-case v)
        "true" true
        "false" false
        nil)))

(defn parse [m f ks]
  (reduce
   (fn [r e]
     (if (contains? r e)
       (update-in r [e] f)
       r))
   m
   ks))

(defn map->properties [m]
  (reduce
   (fn [r [k v]]
     (cond
      (keyword? k) (.put r (name k) (str v))
      (symbol? k) (.put r (name k) (str v))
      :else (.put r (str k) (str v)))
     r)
   (Properties.)
   m))

(defn append [m defaults]
  (merge (apply hash-map defaults) m))

(defmulti read-properties (fn [x & _] (class x)))

(defmethod read-properties Properties [props & {:keys [dump-fn
                                                       required
                                                       parse-int
                                                       parse-boolean
                                                       stringify-keys
                                                       dasherize-keys
                                                       nested
                                                       default]}]
  (-> props
      (properties->map nested (if dasherize-keys dasherize identity))
      (keywordize-keys-unless stringify-keys)
      (parse parse-int-fn parse-int)
      (parse parse-bool-fn parse-boolean)
      (append default)
      (validate required)
      (dump dump-fn)))

(defmethod read-properties BufferedInputStream [stream & x]
  (let [props (input-stream->properties stream)]
    (apply read-properties props x)))

(defmethod read-properties :default [file & x]
  (let [props (file-name->properties file)]
    (apply read-properties props x)))
