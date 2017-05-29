(ns transducer-adducers.basics)

(def printable (into #{} (range 0x20 0x7f)))
(def printable? (partial printable))

;; -----------------------------------------------------------------------------

;; Basic transducer fn shape:
;;
;; (fn [xf]
;;   (fn ([] ...)                ; initialize value
;;       ([result] ...)          ; produce final value
;;       ([result input] ...)))  ; reduce, applying xf as appropriate


;; transformations are composed from basic sequence fns
;; and can be applied to collections, streams, channels, observables, etc.
(def xform (comp (keep printable?) 
                 (map char)))


;; eagerly apply xform, specify a reducing fn and optionally an init val
(transduce xform str (range 0x00 0xff))
(transduce xform str "Printable: " (range 0x00 0xff))


;; eagerly apply xform and specify a final collection type
(into [] xform (range 0x00 0xff))
(into #{} xform (range 0x00 0xff))


;; lazily apply xform
(sequence xform (range 0x00 0xff))
