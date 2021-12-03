package domain

/*

Correcciones
-Polimorfismo para las situaciones
-Cambiar los criterios de elgir puertas a funciones
-Sacar las case class para los grupos y agregar un estadoRecorrido
-Sacar la saludActual o saludBase, dejar solo 1
-Consistencia en la creacion del heroe segun su vida


-Sacar EstadoHeroe
 */

import scala.util.Try
case class GrupoMurioException(grupoMuerto: Grupo) extends Exception("Se murio el grupo")
case class GrupoPerdidoException(grupoPerdido: Grupo) extends Exception ("No hay mas puertas para abrir")
case class RecorridoExitoso(nivel: Int) extends Exception ("Recorrido exitoso con nivel: " + nivel)
trait Condicion extends (Grupo => Boolean)

case class SeEncontroLaSalidaException() extends RuntimeException

case class Calabozo(puertaPrincipal : Puerta, puertaSalida : Puerta) {

}

case class Puerta(habitacion: Habitacion, dificultades : List[Dificultad]) { // si no hay habitacion, la puerta es la salida?
  type Condicion = Grupo => Boolean
  val condicionBase: Condicion = grupo => {
    grupo.heroes.exists(heroe => heroe.trabajo match {
      case Ladrón(habilidadBase) => (habilidadBase + (heroe.nivel * 3)) >= 20
      case _ => false
    })
  }

  def puedoSerAbierta(grupo: Grupo): Boolean = condicionBase(grupo) || dificultades.forall(unaDificultad => unaDificultad.puedenSuperarDificultad(grupo))

  def estaAbierta(): Boolean = dificultades.isEmpty //me parece que no lo usamos

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

trait Dificultad {
  def puedenSuperarDificultad(grupo: Grupo): Boolean = condicionesParaAbrir.exists(condicion => condicion.apply(grupo))
  def condicionesParaAbrir() : List[Condicion]
}

case object Cerrada extends Dificultad {
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = grupo => grupo.cofre.items.contains(Llave)
    val condicion2 : Condicion = grupo => grupo.exists(heroe => heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (heroe.nivel * 3)) >= 10 || grupo.cofre.items.contains(Ganzuas)
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case object Escondida extends Dificultad{
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = grupo => grupo.exists(heroe => heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (heroe.nivel * 3)) >= 6
      case _ => false
    })
    val condicion2 : Condicion = grupo => grupo.exists(heroe => heroe.trabajo match {
      case m : Mago => m.conoceElHechizo(Vislumbrar,heroe.nivel);
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Encantada(hechizoUtilizado: Hechizo) extends Dificultad(){
  override def condicionesParaAbrir() : List[Condicion] = {
    val condicion1 : Condicion = grupo => grupo.exists(heroe => heroe.trabajo match {
      case m : Mago  => m.conoceElHechizo(hechizoUtilizado,heroe.nivel);
      case _ => false
    })
    List(condicion1)
  }
}


case class Habitacion(situacion: Situacion, puertas: List[Puerta]){

  def recorrerHabitacion(grupo: Grupo): Grupo = {
    //Con polimorfismo queda un poco mas claro y no tengo que modificar este codigo
    val grupoListo : Grupo = situacion match{
      case NoPasaNada => grupo;
      case TesoroPerdido(item) => grupo.agregarABotin(item);
      case MuchosMuchosDardos => grupo.transformarHeroes(heroe => heroe.bajarVida(10));
      case TrampaDeLeones => grupo.transformarHeroes( h => h.matarCondicion(grupo.masLento()));
      case Encuentro(heroeExtranjero : Heroe) => {
        type Personalidad = Grupo => Boolean
        val personalidadEncuentro : Personalidad = heroeExtranjero.compatibilidad.criterio;
        val personalidadLider : Personalidad = grupo.getLider.get.compatibilidad.criterio;
        if(personalidadEncuentro.apply(grupo) && personalidadLider.apply(grupo.agregarHeroe(heroeExtranjero))){
          grupo.agregarHeroe(heroeExtranjero)
        } else {
          grupo.pelear(heroeExtranjero)
        }
      }
    }
    grupoListo.recorristeHabitacion(this).verificarVivo
  }

}

trait Situacion
case object NoPasaNada extends Situacion
case class TesoroPerdido(item: Item) extends Situacion
case object MuchosMuchosDardos extends  Situacion
case object TrampaDeLeones extends Situacion
case class Encuentro(heroe: Heroe) extends Situacion
