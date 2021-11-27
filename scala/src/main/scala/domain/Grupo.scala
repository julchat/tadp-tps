package domain;

abstract class Grupo[T <: EstadoHeroe](val heroes: List[T], val cofre: Cofre, val habitacion: Habitacion) {
  def agregarHeroe(heroeExtranjero: Vivo): Grupo[EstadoHeroe] = ???
  //TODO: Mecanica del recorrido del laberinto
  def agregarABotin(item: Item) : Grupo[T] = ???
  /*def map[R <: T](funcion: T => R) : Grupo[R] = ???*/
  def transformarHeroes(funcion: T => T ): Grupo[T] = ???
  def masLento() : EstadoHeroe = ???
  def fuerzaTotal() : Int = ???
  def pelear(heroeExtranjero : EstadoHeroe) : Grupo[EstadoHeroe] = ???
  def getLider() : Option[EstadoHeroe] = ???
  def filter(funcion: T => Boolean) : Grupo[T] = ???
  def exists(funcion: T => Boolean): Boolean = ???
}

case class GrupoVivo[T <: EstadoHeroe](_heroes : List[T], _cofre : Cofre,val _habitacion: Habitacion) extends Grupo(_heroes, _cofre, _habitacion) {
  override def agregarHeroe(heroeExtranjero: Vivo): Grupo[EstadoHeroe] = this.copy(_heroes = _heroes.appended(heroeExtranjero))
  override def filter (funcion: T => Boolean) : Grupo[T] = this.copy(_heroes = _heroes.filter(funcion));
  override def exists (funcion: T => Boolean) : Boolean = this.heroes.exists(funcion)
  override def agregarABotin(item: Item) : Grupo[T] = this.copy(_cofre = _cofre.agregarItem(item));
  def cantidadDeMuertos(): Int = {
    heroes.count { unHeroe => !unHeroe.estoyVivo() };
  }

  def cantidadDeVivos(): Int = {
    heroes.length - cantidadDeMuertos();
  }

  override def getLider(): Option[EstadoHeroe] = heroes.find(_.estoyVivo);;

  override def masLento(): EstadoHeroe = {
    var menor = getLider().get
    heroes.foreach(h => if (h.getVelocidad() < menor.getVelocidad()) {
      menor = h
    })
    menor
  }

  // TODO : Se usa para enfrentar al heroe si no es compatible
  override def fuerzaTotal(): Int = {
    heroes.foldRight[Int](0)(_.heroe.getFuerza()+_)
  }

  // ENCUENTRO ------------------------------------------
  override def pelear(heroeExtranjero: EstadoHeroe): Grupo[EstadoHeroe] = {
    val fuerzaDelExtranjero = heroeExtranjero.getFuerza()
    if (fuerzaTotal() > fuerzaDelExtranjero) {
      this.copy(_heroes = _heroes.map(h => h.subirNivel(1)))
    }
    else {
      // Todos los integrantes(vivos) pierden vida igualitariamente
      this.copy(_heroes = _heroes.map(h => h.perderVida((fuerzaDelExtranjero / cantidadDeVivos()).toInt)))
    }
  }

  // FUNCIONES PARA COMPATIBILIDAD DEL ENCUENTRO
  def hayLadrones(): Boolean = heroes.exists(h => h.esLadron())

  def contieneItem(unItem: Item): Boolean = _cofre.contieneItem(unItem)

  /*override def map[R <: T](funcion: T => R): Grupo[R] = this.copy(_heroes = _heroes.map(unHeroe => funcion.apply(unHeroe)));*/
  override def transformarHeroes(funcion: T => T ): Grupo[T] = this.copy(_heroes = _heroes.map(unHeroe => funcion.apply(unHeroe)))
}


case class GrupoMuerto[T <: EstadoHeroe](val _heroes : List[T],val _cofre : Cofre, _habitacion: Habitacion) extends Grupo(_heroes, _cofre, _habitacion){
  override def agregarHeroe(heroeExtranjero: Vivo): Grupo[EstadoHeroe] = this.copy();
  override def masLento() : EstadoHeroe= ???
  override def fuerzaTotal() :Int = ???
  override def transformarHeroes(funcion: T => T ): Grupo[T] = this.copy();
  override def getLider(): Option[EstadoHeroe] = None
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

  def elegirPuerta(grupo: Grupo[EstadoHeroe]):Puerta = criterioEleccion match {
    case Heroico => grupo.
    case Ordenado =>
    case Vidente =>
    case _ =>
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
  type Personalidad = Grupo[EstadoHeroe] => Boolean
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
