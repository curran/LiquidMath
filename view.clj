(load-file "gui-utils.clj")

					;These values are all derived from the model.

(def compiled-function (ref nil))

					;These functions derive the above values from the model. They are called by the watchers below.

;sin and cos are defined here so they can be accessed by compiled user functions
(def builtins-vector-form '[sin #(. Math sin %)
			    cos #(. Math cos %)])
(defn gen-function-form []
  (let [function (read-string (str "(fn [u v x y z] " @function-str ")"))]
    (list 'let builtins-vector-form function)))
(defn generate-compiled-function []
  (dosync (ref-set compiled-function (eval (gen-function-form)))))

(load-file "view-opengl.clj")

(def text-field (JTextField.))

(def gui-panel (make-gui-panel text-field canvas))

(def frame (make-frame gui-panel 300 350 "Grapher!"))