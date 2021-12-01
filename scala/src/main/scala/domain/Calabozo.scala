package domain

import scala.collection.immutable.Range
import scala.util.Try
case class GrupoMurioException(grupo: Grupo) extends Exception("Se murio el grupo")
case class GrupoPerdidoException(grupo: Grupo) extends Exception ("No hay mas puertas para abrir")
case class RecorridoExitoso(nivel: Int) extends Exception ("Recorrido exitoso con nivel: " + nivel)
case class SeEncontroLaSalidaException() extends RuntimeException

trait Condicion extends (Grupo => Boolean)

class Calabozo(val puertaPrincipal : Puerta, val puertaSalida : Puerta) {
//grupo : estadorrecorrido
/*  def recorrerTodoElCalabozo(grupo : Grupo):Grupo  = {
    val recorrido: Try[Grupo] =
    Try {
      var puertaElegida : Option[Puerta] = Some(puertaPrincipal)
      var grupoModificable : Grupo = grupo.copy(puertaElegida = Some(puertaPrincipal))

      while (grupoModificable.puertaElegida.get != puertaSalida) {
        recorrer(grupoModificable) match{
          case g: Grupo => grupoModificable = g.getLider().get.heroe.elegirPuerta(g);
          case gm : Grupo => print("El grupo ripeo, abortando") ; throw GrupoMurioException(gm)
          case gp: Grupo => print("El grupo se quedo sin puertas para abrir, abortando"); throw GrupoPerdidoException(gp)
        }
      }
      grupoModificable
    }.recover({
        case GrupoMurioException(gm) => gm
        case GrupoPerdidoException(gp) => gp

      })
      recorrido.get
  }*/
/*  def recorrer(grupo: Grupo): Grupo = {
    val unRecorrido : Try[Grupo] = Try {
      grupo.puertaElegida.fold()(puerta => puerta.habitacion.recorrerHabitacion(grupo))
    }.recover({
    })
    unRecorrido.get
  }*/

  def mejorGrupo(grupos: List[Grupo]): Grupo = ??? /*{
    grupos.sortBy(g => this.recorrerTodoElCalabozo(g).puntaje()).last
  }*/

/*  def informarCuantosNivelesNecesitaElGrupo(grupoVivo: Grupo) : Unit = {
  cuantosNivelesNecesitaElGrupo(grupoVivo).fold(println("El grupo fallaba incluso con 20 niveles"))(n => println("El grupo tuvo exito con "+ n +" niveles"))
  }

  def cuantosNivelesNecesitaElGrupo(grupoVivo: Grupo): Option[Int] = {
    val nivelesMaximo: List[Int] = (0 to 20).toList
    nivelesMaximo.find(numero => this.recorrerTodoElCalabozo(grupoVivo.aumentarNiveles(numero)) match {
      case gv : Grupo => true
      case _ => false
    })*/
/*    val buscarNivel: Try[Option[Int]] = Try {
      nivelesMaximo.foldRight(0)((nivel,algo) => this.recorrerTodoElCalabozo(grupoVivo.aumentarNiveles(nivel)) match {
        case GrupoVivo(heroes,cofre,habitacion,puertaElegida) => throw RecorridoExitoso(nivel)
        case _ => nivel
      })
        None
    }.recover({
      case RecorridoExitoso(nivel) => Some(nivel)
    })
    buscarNivel.get
  }*/
}

case class Puerta(habitacion: Habitacion, dificultades : List[Dificultad]) { // si no hay habitacion, la puerta es la salida?
  type Condicion = (Grupo => Boolean)
  val condicionBase: Condicion = grupo => {
    grupo._heroes.exists(heroe => heroe.trabajo match {
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
  def puedenSuperarDificultad(grupo: Grupo): Boolean = condicionesParaAbrir(grupo).exists(condicion => condicion.apply(grupo))

  def condicionesParaAbrir(grupo: Grupo): List[Condicion]
}

case object Cerrada extends Dificultad {
  override def condicionesParaAbrir(grupo: Grupo) : List[Condicion] = {
    val condicion1 : Condicion = grupo => grupo._cofre.items.contains(Llave)
    val condicion2 : Condicion = grupo => grupo.exists(heroe => heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (heroe.nivel * 3)) >= 10 || grupo._cofre.items.contains(Ganzúas)
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case object Escondida extends Dificultad{
  override def condicionesParaAbrir(grupo: Grupo) : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(heroe => heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase + (heroe.nivel * 3)) >= 6
      case _ => false
    })
    val condicion2 : Condicion = (grupo) => grupo.exists(heroe => heroe.trabajo match {
      case m : Mago => m.conoceElHechizo(Vislumbrar,heroe.nivel);
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Encantada(hechizoUtilizado: Hechizo) extends Dificultad(){
  override def condicionesParaAbrir(grupo: Grupo) : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(heroe => heroe.trabajo match {
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
      case MuchosMuchosDardos => grupo.transformarHeroes(unEstadoHeroe => unEstadoHeroe.perderVida(10));
      //case TrampaDeLeones => grupo.transformarHeroes( h => h.matarCondicion(grupo.masLento()));
      case Encuentro(heroeExtranjero : Heroe) => {
        type Personalidad = (Grupo => Boolean)
        val personalidadEncuentro : Personalidad = heroeExtranjero.compatibilidad.criterio;
        val personalidadLider : Personalidad = grupo.getLider().get.compatibilidad.criterio;
        if(personalidadEncuentro.apply(grupo) && personalidadLider.apply(grupo.agregarHeroe(heroeExtranjero))){
          grupo.agregarHeroe(heroeExtranjero)
        } else {
          grupo.pelear(heroeExtranjero)
        }
      }
     //case Encuentro(heroExtranjero : Vivo) => if(grupo.getLider().get.esCompatible(grupo.agregarHeroe(heroExtranjero)) && heroExtranjero.heroe.esCompatible(grupo)) La otra opcion era hacer el pattern matching en esCompatible
    }
    grupoListo.agregarHabitacion(this);
  }

/*  def seguirRecorrido(grupo: GrupoVivo): Grupo = {
    val grupoActualizado = grupo.agregarPuertas(puertas)
    val puertaElegida: Puerta = grupoActualizado.getLider().fold(throw new SeEncontroLaSalidaException())(lider => lider.heroe.elegirPuerta(grupo))

    if(puertaElegida.puedoSerAbierta(grupo)){
      puertaElegida.abrirPuerta().habitacion.fold(throw new SeEncontroLaSalidaException())(habitacion => habitacion.recorrerHabitacion(grupo))
    }
    else{
      GrupoMuerto(heroesMuertos = grupo.heroes, cofre = grupo.cofre)
    }*/
}

trait Situacion
case object NoPasaNada extends Situacion
case class TesoroPerdido(item: Item) extends Situacion
case object MuchosMuchosDardos extends  Situacion
case object TrampaDeLeones extends Situacion
case class Encuentro(heroe: Heroe) extends Situacion
