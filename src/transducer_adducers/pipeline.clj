(ns transducer-adducers.pipeline
  (:require
    [clojure.core.async :as async :refer [<!! chan to-chan pipeline]]
    [clojure.core.reducers :as r]
    [clojure.data.json :as json]
    [clojure.walk :refer [keywordize-keys]]
    [clj-http.client :as http]
    [hickory.core :as hc]
    [hickory.select :as hs :refer [select tag]]))

(def ncpus (.availableProcessors (Runtime/getRuntime)))
(def adv-url "https://api.github.com/repos/richardwilkes/gcs_library/contents/Library/Advantages")

;; -----------------------------------------------------------------------------

;; reducers partition the collection and transform the partitions in parallel
(defn reducer [coll]
  (->> coll
       (r/map keywordize-keys)
       (r/map :download_url)
       (r/map http/get)
       (r/filter (comp #{200} :status))
       (r/map :body)
       (r/map hc/parse)
       (r/map hc/as-hickory)
       (r/mapcat (partial select (tag :advantage)))
       (r/fold 1 r/cat r/append!)))

(def xform
  (comp (map keywordize-keys)
        (map :download_url)
        (map http/get)
        (filter (comp #{200} :status))
        (keep :body)
        (map hc/parse)
        (map hc/as-hickory)
        (mapcat (partial select (tag :advantage)))))

(defn pipeline-process [xform xs]
  (let [cin (to-chan xs) cout (chan)]
    (pipeline (* ncpus 8) cout xform cin)
    (<!! (async/reduce conj [] cout))))

;; -----------------------------------------------------------------------------

(defn time-these [] 
  (let [files (->> adv-url http/get :body json/read-str)]
    (time (prn (count (into [] xform files))))
    (time (prn (count (reducer files))))
    (time (prn (count (pipeline-process xform files))))))
