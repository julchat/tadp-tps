package domain;

case class Grupo(val _heroes : List[Heroe], val _cofre : Cofre, val habitaciones: List[Habitacion] = List(), val puertaElegida : Option[Puerta]) {

  def agregarHabitacion(habitacionRecorrida: Habitacion): Grupo = {
    this.copy(habitaciones = habitaciones.appended(habitacionRecorrida))
  }

  def puntaje(): Int = cantidadDeVivos() * 10 - cantidadDeMuertos() * 5 + tamañoBotin() + nivelMasAlto()
  def tamañoBotin(): Int = _cofre.items.length
  def nivelMasAlto(): Int = _heroes.map(heroe => heroe.nivel).max

  def agregarHeroe(heroeExtranjero: Heroe): Grupo = this.copy(_heroes = _heroes.appended(heroeExtranjero))
  def filter (funcion: Heroe => Boolean) : Grupo = this.copy(_heroes = _heroes.filter(funcion));
  def exists (funcion: Heroe => Boolean) : Boolean = this._heroes.exists(funcion)
  def agregarABotin(item: Item) : Grupo = this.copy(_cofre = _cofre.agregarItem(item));
  def cantidadDeMuertos(): Int = {
    _heroes.count { unHeroe => !unHeroe.estoyVivo() };
  }

  def filtrarPuertasAbribles : List[Puerta] = habitaciones.flatMap(h => h.puertas.filter(p => p.puedoSerAbierta(this)))
  def cantidadDeVivos(): Int = {
    _heroes.length - cantidadDeMuertos();
  }

  def getLider(): Option[Heroe] = _heroes.find(_.estoyVivo())
  def estadoDelGrupo(calabozo: Calabozo) : EstadoRecorrido = {
    if(this._heroes.exists(heroe => heroe.estoyVivo())){
      RecorridoExitoso(calabozo)
    }
    else{
      RecorridoFallido(calabozo);
    }
  }
  //Aca hay que devolver un Vivo
  def masLento(): Heroe = {
    _heroes.sortBy(uh => uh.getVelocidad()).head
    /*var menor = getLider().get
    heroes.foreach(h => if (h.getVelocidad() < menor.getVelocidad()) {
      menor = h
    })
    menor*/
  }
  def conMasNivel(): Heroe = {
    var mayor = getLider().get
    _heroes.foreach(h => if (h.nivel > mayor.nivel) {
      mayor = h
    })
    mayor
  }

  // TODO : Se usa para enfrentar al heroe si no es compatible
  def fuerzaTotal(): Int = {
    _heroes.foldRight[Int](0)(_.getFuerza()+_)
  }

  // ENCUENTRO ------------------------------------------
  def pelear(heroeExtranjero: Heroe): Grupo = {
    val fuerzaDelExtranjero = heroeExtranjero.getFuerza()
    if (fuerzaTotal() > fuerzaDelExtranjero) {
      aumentarNiveles(1)
    }
    else {
      // Todos los integrantes(vivos) pierden vida igualitariamente
      this.transformarHeroes(h => h.perderVida((fuerzaDelExtranjero / cantidadDeVivos()).toInt))
    }
  }

  def hayLadrones(): Boolean = _heroes.exists(h => h.esLadron())

  def contieneItem(unItem: Item): Boolean = _cofre.contieneItem(unItem)

  /*override def map[R <: T](funcion: T => R): Grupo[R] = this.copy(_heroes = _heroes.map(unHeroe => funcion.apply(unHeroe)));*/
  def transformarHeroes(funcion: Heroe => Heroe ): Grupo = this.copy(_heroes = _heroes.map(unHeroe => funcion.apply(unHeroe)))

  def aumentarNiveles(niveles: Int): Grupo = {
    this.copy(_heroes = _heroes.map(h => h.subirNivel(niveles)))
  }
}




case class Cofre(val items : List[Item], val armas : List[String], val tesoroAcumulado : Int) {
  def agregarItem(item: Item): Cofre = this.copy(items = items.appended(item));
  def contieneItem(item: Item): Boolean = items.contains(item);
}

trait Item
case object Ganzúas extends Item
case object Llave extends Item

trait Compatibilidad {
  type Personalidad = Grupo => Boolean
  val criterio: Personalidad
}
case object Introvertido extends Compatibilidad {
  override val criterio: Personalidad = _._heroes.length <= 3
}
case object Bigotes extends Compatibilidad{
  override val criterio: Personalidad = ! _._heroes.exists(h => h.trabajo match {
    case Ladrón(a) => true
    case _ => false
  })
}
case class Interesados(objParticular: Item) extends Compatibilidad{
  override val criterio: Personalidad = _._cofre.contieneItem(objParticular)
}
case object Loquitos extends Compatibilidad{
  override val criterio: Personalidad = _ => false
}

trait Criterio
case object Heroico extends Criterio
case object Ordenado extends Criterio
case object Vidente extends Criterio

abstract class EstadoRecorrido(val calabozo: Calabozo){
  def elegirPuertaYRecorrer(grupo : Grupo): EstadoRecorrido = {
    grupo.getLider().get.elegirPuerta(grupo) match {
      case puerta: Puerta => puerta.get.habitacion.recorrerHabitacion(grupo).estadoDelGrupo(calabozo)
      case None => RecorridoFallido(calabozo)
    };
  }
  def recorrerTodoElCalabozo(grupo: Grupo): EstadoRecorrido = ???
}

case class RecorridoExitoso(val _calabozo: Calabozo) extends EstadoRecorrido(_calabozo){
  override def recorrerTodoElCalabozo(grupo: Grupo): EstadoRecorrido = {
    elegirPuertaYRecorrer(grupo).recorrerTodoElCalabozo(grupo)
  }
}
case class RecorridoFallido(val _calabozo: Calabozo) extends EstadoRecorrido(_calabozo){
  override def recorrerTodoElCalabozo(grupo: Grupo): EstadoRecorrido = {
    print("no hay puerta")
    this
  }
}

