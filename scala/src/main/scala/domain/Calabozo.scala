package domain

import scala.util.Try
case class GrupoMurioException() extends Exception("Se murio el grupo")
trait Condicion extends (Grupo[EstadoHeroe] => Boolean)

case class SeEncontroLaSalidaException() extends RuntimeException

class Calabozo(val puertaPrincipal : Puerta, val puertaSalida : Puerta) {
  //TODO: Modelar como estarian las habitaciones y las puertas aca
  /*  1. Va a ver que puertas estan abiertas
    2. Va a elegir una puerta el lider => si es la de salida tengo que salir
    3. Va a enfrentarse a lo que haya en la situacion de la puerta abierta => cambios en el grupo (tanto de los miembrodis, del botin y de las puertas abiertas)*/
  def puertasVisitadas(): List[Puerta] = ???
  //def recorrerCalabozo(): Habitacion = ???
  //def recorrer(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = puertaPrincipal.recorrer(grupo)
/*  def recorrerTodoElCalabozo(grupo : Grupo[EstadoHeroe]):Grupo[EstadoHeroe]  = {
    val recorrido : Try[Grupo[EstadoHeroe]] = Try {
      while (true) {
        recorrer(grupo)
      }
    }.recover({
      case GrupoMurioException() => GrupoMuerto()
/*      case GrupoSeQuedoSinPuertas =>
      case GrupoExitoso =>*/
    })


    class GrupoSeQuedoSinPuertas() extends Exception
    class GrupoExitoso() extends Exception
      
  }*/


  def recorrer(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    if(puertaPrincipal.puedoSerAbierta(grupo)){
      puertaPrincipal.abrirPuerta().habitacion.fold(throw new SeEncontroLaSalidaException())(habitacion => habitacion.recorrerHabitacion(grupo.agregarPuertas(List(puertaPrincipal))))
    }
    else{
      GrupoMuerto(_heroes = grupo.heroes,_cofre = grupo.cofre,_habitacion = grupo.habitacion,_puertas = grupo.puertas)
    }
  }

}

case class Puerta(habitacion: Option[Habitacion], dificultades : List[Dificultad]) { // si no hay habitacion, la puerta es la salida?
  //type Condicion = (Grupo[EstadoHeroe] => Boolean)
  val condicionBase: Condicion = grupo => {
    grupo.heroes.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase) => (habilidadBase + (estadoHeroe.heroe.nivel * 3)) >= 20
      case _ => false
    })
  }

  def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean = condicionBase(grupo) || dificultades.forall(unaDificultad => unaDificultad.puedenSuperarDificultad(grupo))

  def abrirPuerta() : Puerta = this.copy(dificultades = List())

  def estaAbierta(): Boolean = dificultades.isEmpty

  /*
  def recorrer(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    if(puedoSerAbierta(grupo)){
      val puertaNueva = this.copy().abrirPuerta()
      habitacion.fold(throw new SeEncontroLaSalidaException())(habitacion => habitacion.recorrerHabitacion(grupo.agregarPuertas(List(puertaNueva))))
    }
    else{
      GrupoMuerto(_heroes = grupo.heroes,_cofre = grupo.cofre,_habitacion = grupo.habitacion,_puertas = grupo.puertas)
    }
  }

   */
}

trait Dificultad{ //TODO: Desarrollar las puertas
  def puedenSuperarDificultad(grupo: Grupo[EstadoHeroe]): Boolean = condicionesParaAbrir.exists(condicion => condicion.apply(grupo))
  def condicionesParaAbrir() : List[Condicion]
}

case object Cerrada extends Dificultad {
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.cofre.items.contains(Llave)
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (estadoHeroe.heroe.nivel * 3)) >= 10
      case _ => false
    }) || grupo.cofre.items.contains(Ganzúas)
    List(condicion1,condicion2)
  }
}

case object Escondida extends Dificultad{
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (estadoHeroe.heroe.nivel * 3)) >= 6
      case _ => false
    })
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Mago(hechizosAprendibles) => estadoHeroe.heroe.trabajo.asInstanceOf[Mago].conoceElHechizo(Vislumbrar,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Encantada(hechizoUtilizado: Hechizo) extends Dificultad(){
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Mago(hechizosAprendibles)  => estadoHeroe.heroe.trabajo.asInstanceOf[Mago].conoceElHechizo(hechizoUtilizado,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1)
  }
}


case class Habitacion(situacion: Situacion, puertas: List[Puerta]){

  def recorrerHabitacion(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    situacion match{
      case NoPasaNada => grupo;
      case TesoroPerdido(item) => grupo.agregarABotin(item);
      case MuchosMuchosDardos => grupo.transformarHeroes(unEstadoHeroe => unEstadoHeroe.perderVida(10));
      case TrampaDeLeones => grupo.transformarHeroes( h => h.matarCondicion(grupo.masLento()) );
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

  def seguirRecorrido(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    val grupoActualizado = grupo.agregarPuertas(puertas)
    val puertaElegida: Puerta = grupoActualizado.getLider().fold(throw new SeEncontroLaSalidaException())(lider => lider.heroe.elegirPuerta(grupo))

    if(puertaElegida.puedoSerAbierta(grupo)){
      puertaElegida.abrirPuerta().habitacion.fold(throw new SeEncontroLaSalidaException())(habitacion => habitacion.recorrerHabitacion(grupo))
    }
    else{
      GrupoMuerto(_heroes = grupo.heroes,_cofre = grupo.cofre,_habitacion = grupo.habitacion,_puertas = grupo.puertas)
    }
  }
}

trait Situacion
case object NoPasaNada extends Situacion
case class TesoroPerdido(item: Item) extends Situacion
case object MuchosMuchosDardos extends  Situacion
case object TrampaDeLeones extends Situacion
case class Encuentro(heroe: Vivo) extends Situacion
