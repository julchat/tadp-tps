package domain;

case class Grupo(val _heroes : List[Heroe], val _cofre : Cofre, val habitaciones: List[Habitacion] = List()) {

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
      RecorridoExitoso(this)
    }
    else{
      RecorridoFallidoPorMuerte(this);
    }
  }
  //Aca hay que devolver un Vivo
  def masLento(): Heroe = {
    _heroes.sortBy(uh => uh.getVelocidad()).head
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

  def hayLadrones(): Boolean = _heroes.exists(h => h.trabajo match {
    case l : Ladrón => true
    case _ => false
  })

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

