(ns transducer-adducers.basics)

;; Basic transducer fn shape:
;;
;; (fn [reducing-fn]
;;   (fn ([] ...)                ; initialize value
;;       ([result] ...)          ; produce final value by calling reducing-fn 
;;       ([result input] ...)))  ; reduce, applying reducing-fn as appropriate
;;
;; a transducer is a transformation from one reducing fn to another.
;; 
;; transformations are composed from basic sequence fns
;; and can be applied to collections, streams, channels, observables, etc.
;;
;; recall that comp composes like:
;;   (comp f g h) => (f (g (h _)))
;; 
;; composing a transducer produces a single reducing fn like:
;;   (comp f g h) => ((f (g h)) _)

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
