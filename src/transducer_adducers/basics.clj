(ns transducer-adducers.basics)

;; a transducer is a transformation from one reducing fn to another.
;; 
;; transformations are composed from basic sequence fns
;; and can be applied to collections, streams, channels, observables, etc.
;;
;; basic transducer fn shape:
;;
;; (fn [reducing-fn]
;;   (fn ([] ...)                ; initialize value
;;       ([result] ...)          ; produce final value by calling reducing-fn 
;;       ([result input] ...)))  ; reduce, applying reducing-fn as appropriate
;;
;; a transducer pipeline looks like this:
;;   transduce -> transduce -> transduce -> ... -> reduce

;; -----------------------------------------------------------------------------

;; a transducer needs a final reducing fn to produce a result
;; `into' uses conj internally so you don't have to specify one

(def map-xf (map inc))
(into [] map-xf (range 10))

(def map-filter-xf (comp (map inc) (filter even?)))
(into [] map-filter-xf (range 10))

;; specify your own reducing fn with transduce, which internally does (xf rf)
(transduce map-filter-xf + (range 10))

;; or add it to the transducer and then reduce
(def map-filter-sum (map-filter-xf +))
(reduce map-filter-sum (range 10))

;; -----------------------------------------------------------------------------

(def printable (into #{} (range 0x20 0x7f)))
(def printable? (partial printable))

(def xform (comp (keep printable?) 
                 (map char)))


;; eagerly apply xform, specify a reducing fn and optionally an init val (like reduce)
(transduce xform str (range 0x00 0xff))
(transduce xform str "Printable: " (range 0x00 0xff))


;; eagerly apply xform and specify a final collection type
(into [] xform (range 0x00 0xff))
(into #{} xform (range 0x00 0xff))


;; lazily apply xform (like map)
(sequence xform (range 0x00 0xff))
