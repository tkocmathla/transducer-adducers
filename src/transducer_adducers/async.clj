(ns transducer-adducers.async
  (:require [clojure.core.async :as async
             :refer [<!! chan onto-chan to-chan pipeline]]))

(def ncpus (.availableProcessors (Runtime/getRuntime)))

(def weird-data [1 "foo" \z "xyzzy" 3 4 'meh "DEADBEEF"])

;; -----------------------------------------------------------------------------

(def xform (comp (filter string?) 
                 (map (juxt identity frequencies))))


;; apply xform to elements put in an async channel
(let [ch (chan 1 xform)]
  (onto-chan ch weird-data)
  (take-while identity (repeatedly #(<!! ch))))


;; parallel reduce is possible with async/pipeline
(defn preduce
  [xform rf init coll]
  (let [source-chan (to-chan coll)
        result-chan (chan)]
    (pipeline (* ncpus 8) result-chan xform source-chan)
    (<!! (async/reduce rf init result-chan))))

(preduce xform conj [] weird-data)
