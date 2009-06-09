;this comment is for running within Emacs+SLIME - use c-x c-e after (do ...) to load the jars (default Ubuntu locations)
(comment (do (add-classpath "file:/usr/share/java/jogl.jar") (add-classpath "file:/usr/share/java/gluegen-rt.jar")))

; The model (of the model-view-controller paradigm) code defines the data which represents application state
(load-file "model.clj")

; The view code defines the GUI elements, without any model-changing code attached
(load-file "view.clj")

; The controller code establishes all interactions between the model and view using listeners.
(load-file "controller.clj")

; Initialize the model with default values (which propagates to GUI elements)
(set-model-values {:function-str "(var-set x u) (var-set y v) (var-set z (sin (/ (* u v) 10)))"
		   :num-lines 15
		   :u-min -10
		   :u-max 10
		   :v-min -10
		   :v-max 10})

; Set the size and show the application frame
(.setSize frame 500 500)
(.setVisible frame true)

; Start the animation thread
(.start animator)