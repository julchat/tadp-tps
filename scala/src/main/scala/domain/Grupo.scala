package domain;

case class Grupo(heroes : List[Heroe],cofre : Cofre,habitaciones: List[Habitacion] = List(),puertaElegida : Option[Puerta]) {
  def agregarHeroe(heroeExtranjero: Heroe): Grupo = this.copy(heroes = heroes.appended(heroeExtranjero))
  def filter (funcion: Heroe => Boolean) : Grupo = this.copy(heroes = heroes.filter(funcion))
  def exists (funcion: Heroe => Boolean) : Boolean = this.heroes.exists(funcion)
  def agregarABotin(item: Item) : Grupo = this.copy(cofre = cofre.agregarItem(item))
  def cantidadDeMuertos: Int = heroes.count(unHeroe => !unHeroe.estoyVivo())

  def getLider: Option[Heroe] = heroes.find(_.estoyVivo())
  def recorristeHabitacion(habitacionRecorrida : Habitacion) : Grupo = this.copy(habitaciones = habitaciones.appended(habitacionRecorrida))

  //Aca hay que devolver un Vivo
  def masLento(): Heroe = heroes.minBy(uh => uh.getVelocidad)

  def conMasNivel(): Heroe = heroes.maxBy(h => h.nivel)

  def cantidadDeVivos: Int = heroes.count(unHeroe => unHeroe.estoyVivo())

  def fuerzaTotal(): Int = heroes.foldRight[Int](0)(_.getFuerza+_)

  // ENCUENTRO ------------------------------------------
  def pelear(heroeExtranjero: Heroe): Grupo = {
    val fuerzaDelExtranjero = heroeExtranjero.getFuerza
    if (fuerzaTotal() > fuerzaDelExtranjero) {
      this.copy().aumentarNiveles(1)
    }
    else {
      // Todos los integrantes(vivos) pierden vida igualitariamente
      this.transformarHeroes(h => h.bajarVida(fuerzaDelExtranjero / cantidadDeVivos))
    }
  }

  def hayLadrones(): Boolean = heroes.exists(h => h.esLadron())

  def contieneItem(unItem: Item): Boolean = cofre.contieneItem(unItem)

  def transformarHeroes(funcion: Heroe => Heroe ): Grupo = this.copy(heroes = heroes.map(unHeroe => funcion.apply(unHeroe)))

  def aumentarNiveles(niveles: Int): Grupo = this.copy(heroes = heroes.map(h => h.subirNivel(niveles)))

  def verificarVivo: Boolean = cantidadDeVivos > 0
}


case class Heroe(atributos : Atributos, nivel : Int, saludActual : Int , trabajo : Trabajo, compatibilidad : Compatibilidad, criterioEleccion : Criterio){

  def getFuerza: Int = {
    val adicional : Int =
    trabajo match {
      case Guerrero => (atributos.fuerzaBase * 0.2 * nivel).toInt
      case _ => 0
    }
    atributos.fuerzaBase + adicional
  }

  def getVelocidad: Int = atributos.velocidadBase

  def vidaResultante (vidaAPerder : Int) : Int = math.max(0, saludActual - vidaAPerder)
  def bajarVida (vidaPerdida : Int ) : Heroe = this.copy(saludActual = saludActual - vidaPerdida)
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

  def estoyVivo(): Boolean = saludActual > 0
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
case object Ganzuas extends Item
case object Llave extends Item

trait Compatibilidad {
  type Personalidad = Grupo => Boolean
  val criterio: Personalidad
}
case object Introvertido extends Compatibilidad {
  override val criterio: Personalidad = _.heroes.length <= 3
}
case object Bigotes extends Compatibilidad{
  override val criterio: Personalidad = ! _.heroes.exists(h => h.trabajo match {
    case Ladrón(a) => true
    case _ => false
  })
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
