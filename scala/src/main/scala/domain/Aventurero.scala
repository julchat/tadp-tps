package domain

case class Aventurero(atributos: Atributos, nivel:Int, salud: Int, trabajo: Trabajo){
  require(salud > 0)

  def fuerza: Int = trabajo match {
    case Guerrero => atributos.fuerza + (atributos.fuerza * ((0.2 * nivel) + 1).toInt)
    case _ => atributos.fuerza
  }
}

case class Atributos(fuerza: Int,velocidad: Int)

trait Trabajo
case object Guerrero extends Trabajo
case class Ladron(habilidad: Int) extends Trabajo
case class Mago(hechizos: List[HechizoTupla]) extends Trabajo

case class HechizoTupla(hechizo: Hechizo, nivelRequerido: Int){
  def conoceHechizo(hechizoAConocer: Hechizo, nivelActual: Int): Boolean = this.hechizo.equals(hechizoAConocer) && nivelActual >= nivelRequerido
}

trait Hechizo
case object Invisibilidad extends Hechizo
case object Vislumbrar extends Hechizo


case class Grupo(heroes: List[Aventurero], cofre: List[Item]){
  def hayLadronConHabilidadOMas(listaHeroes:List[Trabajo] ,cantidadHabilidad: Int): Boolean = {
    listaHeroes match {
      case Ladron(habilidad) :: otrosHeroes => habilidad >= cantidadHabilidad
      case _ :: otrosHeroes => hayLadronConHabilidadOMas(otrosHeroes,cantidadHabilidad)
      case _ => false
    }
  }
}

trait Item
case object Llave extends Item
case object Ganzuas extends Item