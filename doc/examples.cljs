;; Colorization functions, demonstrated with ranges
(map #(colorize % (square 50))
     (map #(hsl % 100 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 % 50) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(hsl 120 50 %) (range 0 100 10)))

(map #(colorize % (square 50))
     (map #(rgb % 0 0) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(rgb 0 % 0) (range 0 250 25)))

(map #(colorize % (square 50))
     (map #(rgb 0 0 %) (range 0 250 25)))

;; variable opacity
(map
  #(opacity % (colorize "blue" (square 10)))
  (range 0 1 0.1))

;; color gradient
(apply above
  (map #(colorize (hsl (rescale % 0 1000 120 220) 90 90)
                (rectangle 500 5))
     (range 0 1000 5)))
 
;; rolling triangle rainbow
(apply layer 
  (concat [(colorize "white" (rectangle 750 75))]
          (map #(->> (triangle 25)
                     (position (* 2 %) 20)
                     (rotate %)
   	                 (colorize (hsl % 90 75)))
               (range 0 360 15))))

;; cartwheel rectangles
(layer
  (fill "white" (rectangle 750 70))
  (apply beside  
       (map #(->> (rectangle 30 50)                                          
                  (rotate %)                    	 
                  (opacity 0.5) 	                 
                  (fill (hsl % 90 45)))               
            (range 0 375 15))))

;; confetti
(let [palette (cycle ["aqua" "springgreen" "magenta"])]
  (->> (repeat 20 (triangle 20))
     (map #(position [(rand-int 500) (rand-int 500)] %))
     (map colorize palette)
     (apply group)))

;; Halloween pumpkin
(layer
 (position 40 60 (colorize "orange" (circle 40)))
 (position 10 30 (colorize "black" (triangle 24)))
 (position 45 30 (colorize "black" (triangle 24)))
 (position 20 75 (colorize "black" (rectangle 40 10)))
 (position 25 74 (colorize "orange" (rectangle 10 5)))
 (position 45 74 (colorize "orange" (rectangle 10 5)))
 (position 33 82 (colorize "orange" (rectangle 10 5)))
 (position 35 2 (colorize "black" (rectangle 10 20))))

;; Berlin Fernseheturm
(layer (position 35 60 (colorize "grey" (circle 25)))
       (position 34 0 (colorize "grey" (rectangle 2 300)))
       (position 29 78 (colorize "grey" (rectangle 12 222)))
       (position 29 20 (colorize "grey" (rectangle 12 16)))
       (position 18 285 (colorize "grey" (triangle 35)))
       (position 26 95 (colorize "grey" (rectangle 18 16)))
       (position 24 152 (colorize "grey" (rectangle 5 158)))
       (position 41 152 (colorize "grey" (rectangle 5 158)))
       (position 41 140 (colorize "grey" (rectangle 2 158)))
       (position 27 140 (colorize "grey" (rectangle 2 158))))

;; Berlin Fernseheturm, with lighting effects (beta)
(layer (position 35 90 (colorize "grey" (circle 25)))
       (position 34 0 (colorize "grey" (rectangle 4 300)))
       (position 29 108 (colorize "grey" (rectangle 12 222)))
       (position 29 50 (colorize "grey" (rectangle 12 16)))
       (position 15 315 (colorize "grey" (triangle 40)))
       (position 26 125 (colorize "grey" (rectangle 18 16)))
       (position 24 182 (colorize "grey" (rectangle 5 158)))
       (position 41 182 (colorize "grey" (rectangle 5 158)))
       (position 41 170 (colorize "grey" (rectangle 2 158)))
       (position 27 170 (colorize "grey" (rectangle 2 158)))
       ;; now we start the highlight layer
       (position 25 100 (colorize "lightgrey" (circle 10)))
       (position 24 182 (colorize "lightgrey" (rectangle 5 158)))
       (position 34 0 (colorize "lightgrey" (rectangle 2 50)))
       (position 25 90 (colorize "pink" (rectangle 2 25)))
       (position 22 90 (colorize "pink" (rectangle 2 22)))
       (position 19 90 (colorize "pink" (rectangle 2 19)))
       (position 16 90 (colorize "pink" (rectangle 2 16)))
       (position 13 90 (colorize "pink" (rectangle 2 13)))
       ;; orange highlights
       (position 27 182 (colorize "orange" (rectangle 1 158)))
       (position 31 141 (colorize "orange" (rectangle 1 30))))
