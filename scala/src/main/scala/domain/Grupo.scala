package domain

case class Grupo[T <: EstadoHeroe](val cofre: Cofre){ //TODO: Mecanica del recorrido del laberinto
  def agregarABotin(item: Item) : Grupo[T] = this.copy(cofre = cofre.agregarItem(item));
  def map(funcion: T => T) : Grupo[T] = this.copy();

  def masLento() : EstadoHeroe;
}

case class GrupoVivo[T <: EstadoHeroe](val heroes: List[T], val _cofre : Cofre) extends Grupo(_cofre) {
   def cantidadDeMuertos() : Int  = {
    heroes.filter({unHeroe => unHeroe.estoyVivo()}).length;
  }
  def getLider() : EstadoHeroe = {
    heroes.find(_.estoyVivo).get;
  }
  def masLento() :EstadoHeroe = {
    var menor = getLider()
    heroes.foreach( h => if (h.getVelocidad() <  menor.getVelocidad() ){menor = h})
    menor
  }
  override def map(funcion: T => T): Grupo[T] = this.copy(heroes = heroes.map(unHeroe => funcion.apply(unHeroe)));
}

case class GrupoMuerto[T <: EstadoHeroe](val _cofre : Cofre) extends Grupo(_cofre){
  def masLento() : EstadoHeroe= ???
}

abstract case class EstadoHeroe(val heroe : Heroe){
  def estoyVivo() : Boolean;
  def perderVida(vidaAPerder : Int) : EstadoHeroe;
  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe;
}

case class Vivo(val _heroe : Heroe) extends EstadoHeroe(_heroe) {
  def estoyVivo() : Boolean = true;
  override def perderVida(vidaAPerder : Int) : EstadoHeroe = {
    if (_heroe.vidaResultante(vidaAPerder) > 0){
      this.copy(_heroe = _heroe.bajarVida(vidaAPerder));
    } else {
      this.morir();
    }
  }

  def morir() : EstadoHeroe = {
    val nuevoHeroe : Heroe = _heroe.copy(saludActual = 0);
    Muerto(_heroe = nuevoHeroe);
  }

  def getVelocidad() : Int = _heroe.atributos.velocidadBase;
  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe ={
    if (this == condicion) {
      this.morir()
    }else
      this.copy()
  }
}

case class Muerto(val _heroe : Heroe) extends EstadoHeroe (_heroe){
  def estoyVivo() : Boolean = false;

  override def perderVida(vidaAPerder: Int): EstadoHeroe = ???
  def matarCondicion(condicion: EstadoHeroe) : EstadoHeroe = ???
}

case class Heroe(val atributos : Atributos, val nivel : Int, val saludActual : Int,val trabajo : Trabajo){
  //TODO: Agregar estrategia de planificacion de recorrido por si es el lider
  def getFuerza() : Double = {
    val adicional : Double =
    trabajo match {
    case Guerrero => atributos.fuerzaBase * 0.2 * nivel
    case _ => 0
  }
  atributos.fuerzaBase + adicional
  }
  def vidaResultante (vidaAPerder : Int) : Int = math.max(0, saludActual - vidaAPerder);
  def bajarVida (vidaPerdida : Int ) : Heroe = this.copy(saludActual = saludActual - vidaPerdida);
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

case class Cofre(val items : List[Item], val armas : List[String], val tesoroAcumulado : Int) {
  def agregarItem(item: Item): Cofre = this.copy(items = items.appended(item));

}

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
