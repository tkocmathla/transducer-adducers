(ns transducer-adducers.basics)

(def printable (into #{} (range 0x20 0x7f)))
(def printable? (partial printable))

;; -----------------------------------------------------------------------------

;; transformations are composed from basic sequence fns
(def xform (comp (keep printable?) (map char)))

;; eagerly apply xform, specify a reducing fn and optionally an init val:
(transduce xform str (range 0x00 0xff))
(transduce xform str "Printable: " (range 0x00 0xff))

;; eagerly apply xform and specify a final collection type:
(into [] xform (range 0x00 0xff))
(into #{} xform (range 0x00 0xff))

;; lazily apply xform:
(sequence xform (range 0x00 0xff))
