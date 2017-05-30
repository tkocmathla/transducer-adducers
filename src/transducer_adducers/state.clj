(ns transducer-adducers.state)

;; most transducer fns take a reducing fn as an argument.
;; 
;; some sequence fns return a _stateful_ transducer,
;; which require state to do the reduction. 
;; 
;; in this case, take returns a stateful transducer fn
;; and conj is passed to it as the reducing fn.
(let [rf ((take 10) conj)]
  (reduce rf [] (range 100))  ; => [0 1 2 3 4 5 6 7 8 9]
  (reduce rf [] (range 100))) ; => [] ; oh noes!


;; transduce separates the transforming fn from the reducing fn,
;; and is safe to repeat
(let [xform (take 10)]
  (transduce xform conj [] (range 100))
  (transduce xform conj [] (range 100)))


;; eduction captures a transformation to a specific collection
;; in an iterable and is applied each time it is reduced
(let [ed (eduction (take 10) (range 100))]
  (reduce conj [] ed)
  (reduce conj [] ed))
