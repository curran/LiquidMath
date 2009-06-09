;The following refs (mutable objects) define the model
(def function-str (ref ""))
(def num-lines (ref 0))
(def time-value (atom 0))
(def window (ref {:u-min 0
		  :u-max 0
		  :v-min 0
		  :v-max 0}))

					;The following functions are accessors for the model
					;function-str, num-lines, and time are directly accessed (this seems somehow wrong, but causes no problems)
(defn u-min [] (:u-min @window))
(defn u-max [] (:u-max @window))
(defn v-min [] (:v-min @window))
(defn v-max [] (:v-max @window))

(defn get-model-values []
   (merge @window {:function-str @function-str
		   :num-lines @num-lines}))

(defn update [ref val]
  (when (not (= @ref val)) (dosync (ref-set ref val))))
(def set-function-str #(update function-str %))
(def set-num-lines #(update num-lines %))
(def set-window #(update window %))
(defn set-model-values [vals]
  (dosync (ref-set num-lines (:num-lines vals))
	  (ref-set window (select-keys vals [:u-min :u-max :v-min :v-max]))
	  (ref-set function-str (:function-str vals))))
