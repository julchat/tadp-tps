package domain;

abstract class Grupo() {
  /*def agregarHeroe(heroeExtranjero: Vivo): Grupo = ???*/
  //TODO: Mecanica del recorrido del laberinto
  /*def agregarABotin(item: Item) : Grupo = ???*/
  def cantidadDeVivos(): Int
  def cantidadDeMuertos() : Int
  def tamañoBotin() : Int
  def nivelMasAlto() : Int
  def puntaje(): Int = cantidadDeVivos() * 10 - cantidadDeMuertos() * 5 + tamañoBotin() + nivelMasAlto()

  /*def transformarHeroes(funcion: T => T ): Grupo[T] = ???*/
  /*def map[R <: T](funcion: T => R) : Grupo[R] = ???
  def masLento() : EstadoHeroe = ???
  def conMasNivel(): EstadoHeroe
  def fuerzaTotal() : Int = ???
  def pelear(heroeExtranjero : EstadoHeroe) : Grupo[EstadoHeroe] = ???
  def getLider() : Option[EstadoHeroe] = ???
  def filter(funcion: T => Boolean) : Grupo[T] = ???
  def exists(funcion: T => Boolean): Boolean = ???
  def agregarPuertas(puertasNuevas: List[Puerta]): Grupo[T]
  */
}


case class GrupoVivo(val heroes : List[EstadoHeroe], val cofre : Cofre,val habitaciones: List[Habitacion] = List(), val puertaElegida : Option[Puerta]) extends Grupo() {
  def agregarHeroe(heroeExtranjero: Vivo): GrupoVivo = this.copy(heroes = heroes.appended(heroeExtranjero))
  def filter (funcion: EstadoHeroe => Boolean) : GrupoVivo = this.copy(heroes = heroes.filter(funcion));
  def exists (funcion: EstadoHeroe => Boolean) : Boolean = this.heroes.exists(funcion)
  def agregarABotin(item: Item) : GrupoVivo = this.copy(cofre = cofre.agregarItem(item));
  def cantidadDeMuertos(): Int = {
    heroes.count { unHeroe => !unHeroe.estoyVivo() };
  }

  def filtrarPuertasAbribles : List[Puerta] = habitaciones.flatMap(h => h.puertas.filter(p => p.puedoSerAbierta(this)))
   override def cantidadDeVivos(): Int = {
    heroes.length - cantidadDeMuertos();
  }

  def getLider(): Option[EstadoHeroe] = heroes.find(_.estoyVivo())
  def recorristeHabitacion(habitacionRecorrida : Habitacion) : GrupoVivo = this.copy(habitaciones = habitaciones.appended(habitacionRecorrida))
  def masLento(): EstadoHeroe = {
    var menor = getLider().get
    heroes.foreach(h => if (h.getVelocidad() < menor.getVelocidad()) {
      menor = h
    })
    menor
  }
  def conMasNivel(): EstadoHeroe = {
    var mayor = getLider().get
    heroes.foreach(h => if (h.heroe.nivel > mayor.heroe.nivel) {
      mayor = h
    })
    mayor
  }

  // TODO : Se usa para enfrentar al heroe si no es compatible
  def fuerzaTotal(): Int = {
    heroes.foldRight[Int](0)(_.heroe.getFuerza()+_)
  }

  def verificarVivo() : Grupo = {
    if (cantidadDeVivos() != 0)
    this.copy()
    else
      GrupoMuerto(heroes,cofre)
  }

  // ENCUENTRO ------------------------------------------
  def pelear(heroeExtranjero: EstadoHeroe): GrupoVivo = {
    val fuerzaDelExtranjero = heroeExtranjero.getFuerza()
    if (fuerzaTotal() > fuerzaDelExtranjero) {
      this.copy(heroes = heroes.map(h => h.subirNivel(1)))
    }
    else {
      // Todos los integrantes(vivos) pierden vida igualitariamente
      this.transformarHeroes(h => h.perderVida((fuerzaDelExtranjero / cantidadDeVivos()).toInt))
    }
  }

  def hayLadrones(): Boolean = heroes.exists(h => h.esLadron())

  def contieneItem(unItem: Item): Boolean = cofre.contieneItem(unItem)

  /*override def map[R <: T](funcion: T => R): Grupo[R] = this.copy(_heroes = _heroes.map(unHeroe => funcion.apply(unHeroe)));*/
  def transformarHeroes(funcion: EstadoHeroe => EstadoHeroe ): GrupoVivo = this.copy(heroes = heroes.map(unHeroe => funcion.apply(unHeroe)))

  override def puntaje(): Int = cantidadDeVivos() * 10 - cantidadDeMuertos() * 5 + cofre.items.size + conMasNivel().heroe.nivel

  override def tamañoBotin(): Int = cofre.items.size

  override def nivelMasAlto(): Int = heroes.map(h => h.heroe.nivel).max
}


case class GrupoMuerto(heroesMuertos: List[EstadoHeroe], cofre : Cofre ) extends Grupo(){

  override def cantidadDeVivos(): Int = 0

  override def cantidadDeMuertos(): Int = heroesMuertos.length

  override def tamañoBotin(): Int = cofre.items.size

  override def nivelMasAlto(): Int = heroesMuertos.map(h => h.heroe.nivel).max
}

case class GrupoPerdido() extends Grupo() {

  override def cantidadDeVivos(): Int = ???

  override def cantidadDeMuertos(): Int = ???

  override def tamañoBotin(): Int = ???

  override def nivelMasAlto(): Int = ???
}

abstract class EstadoHeroe(val heroe : Heroe){
  def estoyVivo() : Boolean;
  def perderVida(vidaAPerder : Int) : EstadoHeroe;
  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe;
  def getVelocidad() : Int = heroe.atributos.velocidadBase;
  def getFuerza() : Int = heroe.getFuerza;
  def esLadron() = heroe.esLadron();
  def subirNivel(niveles : Int) : EstadoHeroe;
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

  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe ={
    if (this == condicion) {
      this.morir()
    }else
      this.copy()
  }

  def subirNivel(niveles : Int) :EstadoHeroe = this.copy(_heroe.subirNivel(niveles)); // o bien _heroe = ***
}

case class Muerto(val _heroe : Heroe) extends EstadoHeroe (_heroe){
  def estoyVivo() : Boolean = false;

  override def perderVida(vidaAPerder: Int): EstadoHeroe = ???
  def matarCondicion(condicion: EstadoHeroe) : EstadoHeroe = ???
  def subirNivel(niveles : Int) :EstadoHeroe = ???
  override def getVelocidad() : Int = ???
  override def esLadron(): Boolean = ???
  override def getFuerza(): Int = ???
}

case class Heroe(val atributos : Atributos, val nivel : Int, val saludActual : Int,val trabajo : Trabajo,val compatibilidad : Compatibilidad, val criterioEleccion : Criterio){
  //TODO: Agregar estrategia de planificacion de recorrido por si es el lider

  def getFuerza() : Int = {
    val adicional : Int =
    trabajo match {
    case Guerrero => (atributos.fuerzaBase * 0.2 * nivel).toInt
    case _ => 0
  }
    atributos.fuerzaBase + adicional
  }
  def vidaResultante (vidaAPerder : Int) : Int = math.max(0, saludActual - vidaAPerder);
  def bajarVida (vidaPerdida : Int ) : Heroe = this.copy(saludActual = saludActual - vidaPerdida);
  def subirNivel(nivelesGanados: Int) : Heroe = this.copy(nivel = nivel + nivelesGanados)

  def esLadron() : Boolean ={
    trabajo match {
      case Ladrón(habilidadBase) => true
      case _ => false
    }
  }

  def elegirPuerta(grupo: GrupoVivo):GrupoVivo = criterioEleccion match {
    case Heroico => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.lastOption)
    case Ordenado => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.headOption)
    case Vidente => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.sortBy(p => p.habitacion.recorrerHabitacion(grupo).puntaje()).headOption)
  }

  // DONDE PONGO ESTO??
/*  def esCompatible(grupo: GrupoVivo) : Boolean = {
    compatibilidad match{
      case introvertidos =>  grupo.length <= 3;
      case bigotes => // que le caen bien los grupos donde no hay ladrones
      case interesados(objParticular: Item) => // se suman a un grupo solamente si tiene cierto objeto  particular que le interesa.
      case loquitos => //siempre van a querer pelearse porque no les cae bien nadie
    }
  }*/
}

case class Atributos(val fuerzaBase : Int, val velocidadBase : Int, val saludBase : Int)

trait Trabajo

case object Guerrero extends Trabajo
case class Ladrón(val habilidadBase : Int) extends Trabajo
case class Mago(val hechizosAprendibles : List[HechizoAprendible]) extends Trabajo{
  def conoceElHechizo(hechizo : Hechizo, nivelMago : Int) : Boolean = {
  hechizosAprendibles.exists(h => h.hechizo == hechizo && h.conoceElHechizo(nivelMago))
  }
}

case class HechizoAprendible(val nivelRequerido : Int, val hechizo : Hechizo){
  def conoceElHechizo(nivel : Int) : Boolean = {
    nivel >= nivelRequerido
  }
}

trait Hechizo
case object Vislumbrar extends Hechizo



case class Cofre(val items : List[Item], val armas : List[String], val tesoroAcumulado : Int) {
  def agregarItem(item: Item): Cofre = this.copy(items = items.appended(item));
  def contieneItem(item: Item): Boolean = items.contains(item);
}

trait Item
case object Ganzúas extends Item
case object Llave extends Item

trait Compatibilidad {
  type Personalidad = GrupoVivo => Boolean
  val criterio: Personalidad
}
case object Introvertido extends Compatibilidad {
  override val criterio: Personalidad = _.heroes.length <= 3
}
case object Bigotes extends Compatibilidad{
  override val criterio: Personalidad = _.heroes.find(h => h.heroe.trabajo match {
    case Ladrón(a) => true
    case _ => false
  }) match {
    case Some(a) => false
    case None => true
  }
}
case class Interesados(objParticular: Item) extends Compatibilidad{
  override val criterio: Personalidad = _.cofre.contieneItem(objParticular)
}
case object Loquitos extends Compatibilidad{
  override val criterio: Personalidad = _ => false
}

trait Criterio
case object Heroico extends Criterio
case object Ordenado extends Criterio
case object Vidente extends Criterio

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
