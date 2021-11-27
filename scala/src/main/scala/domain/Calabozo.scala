package domain

class Calabozo(val puertaPrincipal : Puerta, val puertaSalida : Puerta) {
  //TODO: Modelar como estarian las habitaciones y las puertas aca
  /*  1. Va a ver que puertas estan abiertas
    2. Va a elegir una puerta el lider => si es la de salida tengo que salir
    3. Va a enfrentarse a lo que haya en la situacion de la puerta abierta => cambios en el grupo (tanto de los miembros, del botin y de las puertas abiertas)*/
  def puertasVisitadas(): List[Puerta] = ???
  def recorrerCalabozo(): Habitacion = ???
}

case class Puerta( val habitacionLadoA: Habitacion, val habitacionLadoB: Option[Habitacion], val dificultades : List[Dificultad]) {
  type Condicion = (Grupo[EstadoHeroe] => Boolean)
  val condicionBase: Condicion = (grupo) => {
    grupo.heroes.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase) => (habilidadBase * estadoHeroe.heroe.nivel) >= 20
      case _ => false
    })
  }

  def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean =
    if (dificultades.isEmpty) condicionBase.apply(grupo) || dificultades.forall(unaDificultad => unaDificultad.puedenSuperarDificultad(grupo))
    else true

  def abrirPuerta() : Puerta = this.copy(dificultades = List())
}

trait Dificultad{ //TODO: Desarrollar las puertas
  type Condicion = (Grupo[EstadoHeroe] => Boolean)
  def puedenSuperarDificultad(grupo: Grupo[EstadoHeroe]): Boolean = condicionesParaAbrir.exists(condicion => condicion.apply(grupo))
  def condicionesParaAbrir() : List[Condicion] = ???
}

case class Cerrada() extends Dificultad() {
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.cofre.items.contains(Llave)
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase * estadoHeroe.heroe.nivel) >= 10
      case _ => false
    })
    val condicion3: Condicion = grupo => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match{
      case Ladrón(habilidadBase) => grupo.cofre.items.contains(Ganzúas)
      case _ => false
    })
    List(condicion1,condicion2,condicion3)
  }
}

case class Escondida() extends Dificultad(){
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase * estadoHeroe.heroe.nivel) >= 6
      case _ => false
    })
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Mago(hechizosAprendibles)  => estadoHeroe.heroe.trabajo.asInstanceOf[Mago].conoceElHechizo(Vislumbrar,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Encantada(hechizoUtilizado: Hechizo) extends Dificultad(){
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case  Mago(hechizosAprendibles)  => estadoHeroe.heroe.trabajo.asInstanceOf[Mago].conoceElHechizo(hechizoUtilizado,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1)
  }
}



case class Habitacion(val situacion : Situacion,val puertas : List[Puerta]){ //TODO: Terminar las habitaciones

  def recorrerHabitacion(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    situacion match{
      case NoPasaNada => grupo;
      case TesoroPerdido(item) => grupo.agregarABotin(item);
      case MuchosMuchosDardos => grupo.transformarHeroes(unEstadoHeroe => unEstadoHeroe.perderVida(10));
      case TrampaDeLeones => grupo.transformarHeroes( h => h.matarCondicion(grupo.masLento()) );
      //case TrampaDeLeones => grupo.agregarABotin(Llave);//Map no es del to-do util porque tenemos que analizar por to-do el conjunto
                              //Hacer un fold o un reduce para conseguir al mas lento. Primero habria que filtrar los vivos
                              //Dentro de la funcion del fold podria usarse para la comparacion que este vivo
      // case Encuentro(personalidad) => grupo //Map con aplicacion parcial para ver como se lleva con el lider?
      case Encuentro(heroeExtranjero : Vivo) => {
        type Personalidad = (Grupo[EstadoHeroe] => Boolean)
        val personalidadEncuentro : Personalidad = heroeExtranjero.heroe.compatibilidad.criterio;
        val personalidadLider : Personalidad = grupo.getLider().get.heroe.compatibilidad.criterio;
        if(personalidadEncuentro.apply(grupo) && personalidadLider.apply(grupo.agregarHeroe(heroeExtranjero))){
          grupo.agregarHeroe(heroeExtranjero)
        } else {
          grupo.pelear(heroeExtranjero)
        }
      }
     //case Encuentro(heroExtranjero : Vivo) => if(grupo.getLider().get.esCompatible(grupo.agregarHeroe(heroExtranjero)) && heroExtranjero.heroe.esCompatible(grupo)) La otra opcion era hacer el pattern matching en esCompatible
    }
  }
}

trait Situacion
case object NoPasaNada extends Situacion
case class TesoroPerdido(item: Item) extends Situacion
case object MuchosMuchosDardos extends  Situacion
case object TrampaDeLeones extends Situacion
case class Encuentro(heroe: Vivo) extends Situacion
