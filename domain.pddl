(define (domain multi-snake) (:requirements :strips :typing :negative-preconditions)

  (:types
    agent casella
  )

  (:predicates
    (en ?a - agent ?p - casella)
    (lliure ?p - casella)
    (sortida ?a - agent ?p - casella)
    (fora ?a - agent)
    (adjacent ?p1 - casella ?p2 - casella)
  )

  (:action moure
    :parameters (
      ?a - agent
      ?origen - casella
      ?desti - casella
    )
    
    :precondition (and
      (en ?a ?origen)
      (lliure ?desti)
      (adjacent ?origen ?desti)
      (not (fora ?a))
      (not (sortida ?a ?desti))
    )

    :effect (and
      (not (en ?a ?origen))
      (not (lliure ?desti))
      (en ?a ?desti)
    )
  )

  (:action sortir
    :parametres (
      ?a - agent
      ?origen - casella
      ?desti - casella
    )

    :precondition (and
      (en ?a ?origen)
      (lliure ?desti)
      (adjacent ?origen ?desti)
      (sortida ?a ?desti)
      (not (fora ?a))
    )

    :efect (and
      (not (en ?a ?origen))
      (not (lliure ?desti))
      (fora ?a)
    )
  )
)