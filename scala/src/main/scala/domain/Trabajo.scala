package domain

// Trabajos

object Guerrero{
  val fuerza: Aventurero => Aventurero = aventurero => aventurero.copy(fuerza=fuerza+0.2*aventurero.nivel)
}
class Ladron(_habilidadManos:Int,nivel:Int){
val habilidadManos:Int = _habilidadManos+3*nivel
}
