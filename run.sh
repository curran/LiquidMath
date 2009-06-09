#!/bin/sh
java -cp $CLOJURE_EXT/clojure.jar:/usr/share/java/jogl.jar:/usr/share/java/gluegen-rt.jar:./ clojure.main main.clj
