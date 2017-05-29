(ns transducer-adducers.async
  (:require [clojure.core.async :refer [<!! chan onto-chan]]))

(def xform (comp (filter string?) 
                 (map (juxt identity frequencies))))


;; apply xform to elements put in an async channel
(let [ch (chan 1 xform)]
  (onto-chan ch [1 "foo" \z "xyzzy" 3 4 'meh "DEADBEEF"])
  (take-while identity (repeatedly #(<!! ch))))
