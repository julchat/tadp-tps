package domain

case class Aventurero(atributos: Atributos, nivel:Int, salud: Int, trabajo: Trabajo){
  def fuerza(): Int = trabajo match {
    case Guerrero => atributos.fuerza + (atributos.fuerza * ((0.2 * nivel) + 1).toInt)
    case _ => atributos.fuerza
  }
}

case class Atributos(fuerza: Int,velocidad: Int)

trait Trabajo
case object Guerrero extends Trabajo
case class Ladron(habilidad: Int) extends Trabajo
case class Mago(hechizos: List[Hechizo]) extends Trabajo

trait Hechizo
case class Invisibilidad(nivelRequerido: Int)


case class Grupo(heroes: List[Aventurero], cofre: List[Item])

trait Item