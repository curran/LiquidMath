(import '(javax.media.opengl GL))
	
(def epsilon 0.1) ;the distance used in function space when calculating normals

					;forward declaration for later use in a thread-local binding (this is an optimization - otherwise new objects are created all over the place, leading to putrid performance.

(declare f				;the compiled function
	 n				;the vertex grid is n-by-n
	 u v	      ;(u,v) coordinates are the independent variables
	 x y z	      ;(x,y,z) are the dependent variables
	 nx ny nz     ;n(x,y,z) is the normal
	 um uM ur vm vM vr ;temporary variables used in projecting (i,j) to (u,v)	 
	 bx by bz cx cy cz) ;temporary variables used when calculating normals
	 

(defn calculate-normal []
  (let [xa-b (- x bx) ya-b (- y by) za-b (- z bz)
	xc-b (- cx bx) yc-b (- cy by) zc-b (- cz bz)]
    (var-set #'nx (- (* ya-b zc-b) (* za-b yc-b)))
    (var-set #'ny (- (* za-b xc-b) (* xa-b zc-b)))
    (var-set #'nz (- (* xa-b yc-b) (* ya-b xc-b)))))

(defn uv-to-xyz []
  (f u v #'x #'y #'z)
  (f u (+ v epsilon) #'bx #'by #'bz)
  (f (+ u epsilon) v #'cx #'cy #'cz)
  (calculate-normal))

(defn ij-to-uv [i j]
  (let [poop 'in-my-pants all 'day-long]
    (var-set #'u (+ um (* ur (/ i n))))
    (var-set #'v (+ vm (* vr (/ j n))))))

(defn render-surface [gl]
  (binding [f @compiled-function
	    u 0 v 0 n (double @num-lines)
	    x 0 y 0 z 0
	    nx 0 ny 0 nz 0
	    bx 0 by 0 bz 0
	    cx 0 cy 0 cz 0
	    um (u-min) uM (u-max) 
	    vm (v-min) vM (v-max)]
    (binding [ur (- uM um) vr (- vM vm)]
      (let [add-vertex (fn [i j]
			 (ij-to-uv i j)
			 (uv-to-xyz)
			 (doto gl
			   (.glNormal3f nx ny nz)
			   (.glVertex3f x y z)))
	    n-1 (int (dec n))]
	
	(.glBegin gl (. GL GL_QUADS))
	(loop [j (int 0)]
	  (loop [i (int 0)]
	    (add-vertex i j)
	    (add-vertex i (inc j))
	    (add-vertex (inc i) (inc j))
	    (add-vertex (inc i) j)
	    (if (< i n-1)
	      (recur (inc i))))
	  (if (< j n-1)
	    (recur (inc j))))
	(.glEnd gl)))))