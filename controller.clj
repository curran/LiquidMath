(defn update-text-field	[]
  (when (not (= (.getText text-field) @function-str))
    (.setText text-field @function-str)))

(defn update-display-list []
  (dosync (ref-set display-list-needs-updating true)))

(defn link
  "Expects a sequence of refs and a sequence of functions. Adds a watch (via add-watch) to the refs which calls the functions in the order given."
  [refs functions]
  (let [f (fn [k r o n] (doseq [f functions] (f)))]
    (doseq [r refs] (add-watch r (str (gensym)) f))))

(link [function-str] [generate-compiled-function update-text-field update-display-list])

(.addActionListener
 text-field 
 (proxy [ActionListener] []
   (actionPerformed [e] (set-function-str (.getActionCommand e)))))

(.addWindowListener
 frame
 (proxy [WindowAdapter] []
   (windowClosing [e] (.stop animator) (.dispose frame))))