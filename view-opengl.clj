(load-file "surface-renderer.clj")

(import '(java.awt Frame)
        '(java.awt.event WindowListener WindowAdapter MouseAdapter MouseMotionAdapter)
        '(javax.media.opengl GLCanvas GLEventListener GL GLAutoDrawable)
        '(javax.media.opengl.glu GLU)
        '(com.sun.opengl.util Animator))
(def rotateT 0)
(def rotation-factor 0.3)
(def previous-mouse-x (ref 0))
(def previous-mouse-y (ref 0))
(def previous-rotation-x (ref 0.0))
(def previous-rotation-y (ref 0.0))
(def rotation-x (ref 0.0))
(def rotation-y (ref 0.0))
(def rotation-increment-x (ref 0.003))
(def rotation-damping-factor 1.0)

(def display-list-id (ref 0))
(def display-list-needs-updating (ref true))

(def glu (new GLU))
(def canvas (new GLCanvas))
(def animator (new Animator canvas))

(.addGLEventListener
 canvas
 (proxy [GLEventListener] []
   (display
    [#^GLAutoDrawable drawable]
    (let [gl (.getGL drawable)]
      (doto gl
	(.glClear (. GL GL_COLOR_BUFFER_BIT))
	(.glClear (. GL GL_DEPTH_BUFFER_BIT))
	(.glLoadIdentity)
	(.glTranslatef (float 0) (float 0) (float -40))
	(.glRotatef @rotation-y (float 1) (float 0) (float 0))
	(.glRotatef @rotation-x (float 0) (float 0) (float 1)))
      (if @display-list-needs-updating
	(do (.glNewList gl @display-list-id (. GL GL_COMPILE_AND_EXECUTE))
	    (render-surface gl)
	    (.glEndList gl)
	    (dosync (ref-set display-list-needs-updating false)))
	(.glCallList gl @display-list-id)))
    (dosync (alter rotation-x #(+ % @rotation-increment-x))
	    (alter rotation-increment-x #(* % rotation-damping-factor))))
;	    (ref-sef rotation-increment-x 

   (displayChanged [drawable m d])

   (init
    [#^GLAutoDrawable drawable]
    (let [gl (.getGL drawable)]
      (doto gl
	(.glShadeModel (. GL GL_SMOOTH))
	(.glClearColor 0 0 0 0)
	(.glClearDepth 1)
	(.glEnable (. GL GL_DEPTH_TEST))
	(.glEnable (. GL GL_NORMALIZE))
	(.glDepthFunc (. GL GL_LEQUAL))
	(.glHint (. GL GL_PERSPECTIVE_CORRECTION_HINT)
		 (. GL GL_NICEST))

					;	(.glLightModelf (. GL GL_LIGHT_MODEL_TWO_SIDE) (. GL GL_TRUE))
					;	(.glLightModelf (. GL GL_LIGHT_MODEL_AMBIENT) (. GL GL_TRUE))

	(.glEnable (. GL GL_LIGHTING))
	(.glLightfv (. GL GL_LIGHT1) (. GL GL_AMBIENT) (float-array [0.5 0.5 0.5 1.0]) 0)
	(.glLightfv (. GL GL_LIGHT1) (. GL GL_DIFFUSE) (float-array [1.0 1.0 1.0 1.0]) 0)
	(.glLightfv (. GL GL_LIGHT1) (. GL GL_POSITION) (float-array [0.0 0.0 2.0 1.0]) 0)
	(.glEnable (. GL GL_LIGHT1)))

      (dosync (ref-set display-list-id (.glGenLists gl 1)))))

   (reshape
    [#^GLAutoDrawable drawable x y w h]
    (when (> h 0)
      (let [gl (.getGL drawable)]
	(.glMatrixMode gl (. GL GL_PROJECTION))
	(.glLoadIdentity gl)
	(.gluPerspective glu 50 (/ w h) 1 1000)
	(.glMatrixMode gl (. GL GL_MODELVIEW))
	(.glLoadIdentity gl))))))
(.addMouseListener
 canvas
 (proxy [MouseAdapter] []
   (mousePressed [e] (dosync (ref-set previous-mouse-x (.getX e))
			     (ref-set previous-mouse-y (.getY e))
			     (ref-set previous-rotation-x @rotation-x)
			     (ref-set previous-rotation-y @rotation-y)))))
(defn rotate [ref prev-rotation curr-mouse prev-mouse]
  (ref-set ref (+ prev-rotation (* rotation-factor (- curr-mouse prev-mouse)))))
(.addMouseMotionListener
 canvas
 (proxy [MouseMotionAdapter] []
   (mouseDragged [e] (dosync (rotate rotation-x @previous-rotation-x (.getX e) @previous-mouse-x)
			     (rotate rotation-y @previous-rotation-y (.getY e) @previous-mouse-y)))))
