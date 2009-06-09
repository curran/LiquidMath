(import '(javax.swing JFrame JLabel JTextField JButton JPanel)
	'(java.awt.event ActionListener WindowAdapter)
	'(java.awt GridBagLayout GridBagConstraints Color Font RenderingHints))

(defn make-gui-panel [text-field panel]
  (defn make-text-field-constraints []
    (let [c (GridBagConstraints.)]
      (set! (.fill c) (. GridBagConstraints HORIZONTAL))
      (set! (.weightx c) 1)
      c))
  (defn make-panel-constraints []
    (let [c (GridBagConstraints.)]
      (set! (.gridy c) 1)
      (set! (.weighty c) 1)
      (set! (.fill c) (. GridBagConstraints BOTH))
      c))
  (let [gridbag (GridBagLayout.)]
					;set up gridbag constraints
    (doto gridbag
      (.setConstraints text-field (make-text-field-constraints))
      (.setConstraints panel (make-panel-constraints)))
					;add the components to the panel and return it
    (doto (JPanel.)
      (.setLayout gridbag)
      (.add text-field)
      (.add panel))))

(defn make-frame [panel width height frame-title]
  (doto (JFrame. frame-title)
    (.add panel)
    (.setSize width height)))

