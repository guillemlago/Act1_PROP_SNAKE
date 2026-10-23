(define (problem mapa-a)
  (:domain multi-snake)

  (:objects
    agent-a agent-b - agent

    p20 p21 p22 p23 p24 p25 p26
    p31 p32 p34 p35 p36
    p41 p42 p43 p44 p45 p46 p47 - casella
  )

  (:init
    ; Agents
    (en agent-a p26)
    (en agent-b p41)

    ; Caselles lliures
    (lliure p20)
    (lliure p21)
    (lliure p22)
    (lliure p23)
    (lliure p24)
    (lliure p25)
    (lliure p31)
    (lliure p32)
    (lliure p35)
    (lliure p36)

    (lliure p42)
    (lliure p43)
    (lliure p44)
    (lliure p45)
    (lliure p46)
    (lliure p47)

    ; Sortides
    (sortida agent-a p20)
    (sortida agent-b p47)

    ; Adjacencies horitzontals
    (adjacent p20 p21)
    (adjacent p21 p20)
    (adjacent p21 p22)
    (adjacent p22 p21)
    (adjacent p22 p23)
    (adjacent p23 p22)
    (adjacent p23 p24)
    (adjacent p24 p23)
    (adjacent p24 p25)
    (adjacent p25 p24)
    (adjacent p25 p26)
    (adjacent p26 p25)
    
    (adjacent p31 p32)
    (adjacent p32 p31)
    (adjacent p34 p35)
    (adjacent p35 p34)
    (adjacent p35 p36)
    (adjacent p36 p35)

    (adjacent p41 p42)
    (adjacent p42 p41)
    (adjacent p42 p43)
    (adjacent p43 p42)
    (adjacent p43 p44)
    (adjacent p44 p43)
    (adjacent p44 p45)
    (adjacent p45 p44)
    (adjacent p45 p46)
    (adjacent p46 p45)
    (adjacent p46 p47)
    (adjacent p47 p46)

    ; Adjacencies verticals
    (adjacent p21 p31)
    (adjacent p31 p21)

    (adjacent p22 p32)
    (adjacent p32 p22)
    (adjacent p32 p42)
    (adjacent p42 p32)

    (adjacent p24 p34)
    (adjacent p34 p24)
    (adjacent p34 p44)
    (adjacent p44 p34)

    (adjacent p25 p35)
    (adjacent p35 p25)
    (adjacent p35 p45)
    (adjacent p45 p35)

    (adjacent p36 p46)
    (adjacent p46 p36)
  )

  (:goal
    (and
      (fora agent-a)
      (fora agent-b)
    )
  )
)
