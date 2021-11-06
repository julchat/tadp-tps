package domain

case class Heroe(val atributos : Atributos, val nivel : Int, val saludActual : Int,val trabajo : Trabajo){
  def getFuerza() : Double = {
    val adicional : Double =
    trabajo match {
    case Guerrero => atributos.fuerzaBase * 0.2 * nivel
    case _ => 0
  }
  atributos.fuerzaBase + adicional
  }

  def perderVida(vidaPerdida : Int) : Heroe = copy(saludActual = math.max(0, saludActual - vidaPerdida))
}

case class Atributos(val fuerzaBase : Int, val velocidadBase : Int, val saludBase : Int)

trait Trabajo

case object Guerrero extends Trabajo
case class Ladrón(val habilidadBase : Int) extends Trabajo
case class Mago(val hechizosAprendibles : List[Hechizo]) extends Trabajo

case class Hechizo(val nivelRequerido : Int, val nombre : String){
  def conoceElHechizo(nivel : Int) : Boolean = {
    nivel >= nivelRequerido
  }
}

case class Grupo(val heroes : List[Heroe], val cofre : Cofre){
  def getLider : Heroe = heroes.find(h => h.saludActual > 0).get
}
case class Cofre(val items : List[Item], val armas : List[String], val tesoroAcumulado : Int)

trait Item
case object Ganzúas extends Item
case object Llave extends Item

//Como tratar a los muertos? Habría que filtrar mucho. Una mónada seria medio al pepe. No nos gusta la del entero de muertos.


/*
package domain

case class Heroe (val saludActual : Int, val saludMaxima : Int, val nivelActual : Int = 1, val recursos : List[Recurso], trabajo : Trabajo) {
  def subirDeNivel(): Heroe ={
    trabajo.
  }

} //Debería ser una mónada para tener vivos y muertos? No creo que la nocion de un estado terminal valga la pena

trait Trabajo{
    modificar
}

case class Grupo (val heroes : List[Heroe], val cofreComun : Cofre){


}

case class Cofre(val items : List [Recurso], val armas : String, val tesoro : Int){

}

class Recurso //Puede ser: un item, un hechizo, una estadística, una habilidad


object SimuladorDeGrupos{
  def recorrerCalabozoConXNivelesAdicionales(cantidadNivelesExtra : Int, grupoDeHeroes : Grupo, desafio : Calabozo): Grupo = {
    val heroesSubidos : List[Heroe] = grupoDeHeroes.heroes.map(heroe => heroe.subiDeNivel(cantidadNivelesExtra))
    desafio.//fold?
  }
}

class Calabozo(val habitaciones : List[Habitacion]){

}

class Habitacion(val puertas : List [Puerta], val dificultad : Situacion ){

} //Pueden llegar a ser más de 4???

//Son como las actividades las situaciones?*/
