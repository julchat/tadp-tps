package domain;

case class Grupo(val _heroes : List[Heroe], val _cofre : Cofre, val habitaciones: List[Habitacion] = List(), val puertaElegida : Option[Puerta]) {

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
  def recorristeHabitacion(habitacionRecorrida : Habitacion) : Grupo = {
    val grupoNuevo: Grupo = this.copy(habitaciones = habitaciones.appended(habitacionRecorrida))
    println("Se agrego una habitacion recorrida " + grupoNuevo.habitaciones);
    grupoNuevo
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

case class Heroe(val atributos : Atributos, val nivel : Int, val saludActual : Int ,val trabajo : Trabajo,val compatibilidad : Compatibilidad, val criterioEleccion : Criterio){
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
 //atributo de la funcion para criterioEleccion a la cual le apliquemos el grupo
  //refactor para abstraer lo que esta en comun
  def elegirPuerta(grupo: Grupo):Grupo = criterioEleccion match {
    case Heroico => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.lastOption)
    case Ordenado => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.headOption)
    case Vidente => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.sortBy(p => p.habitacion.recorrerHabitacion(grupo).puntaje()).lastOption)
  }

  def estoyVivo() : Boolean = {
    !(saludActual == 0);
  }
 // def matarCondicion(condicion: Heroe): Heroe;
  def getVelocidad() : Int = atributos.velocidadBase;


  def perderVida(vidaAPerder : Int) : Heroe = {
    if (vidaResultante(vidaAPerder) > 0){
      bajarVida(vidaAPerder);
    } else {
      bajarVida(saludActual)
    }
  }

  /*  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe ={
      if (this == condicion) {
        this.morir()
      }else
        this.copy()
    }*/



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

case class Atributos(val fuerzaBase : Int, val velocidadBase : Int)

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

