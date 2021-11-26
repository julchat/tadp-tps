package domain

import scala.Specializable.Group

class Calabozo(val puertaPrincipal : Puerta){
//TODO: Modelar como estarian las habitaciones y las puertas aca

}


trait Puerta{ //TODO: Desarrollar las puertas
  val habitacionLadoA : Habitacion
  val habitacionLadoB : Option[Habitacion]
  type Condicion = (Grupo[EstadoHeroe] => Boolean)
  val condicionBase : Condicion = (grupo) => {
    grupo.heroes.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase) => (habilidadBase * estadoHeroe.heroe.nivel) >= 20
      case _ => false
    })
  }
  def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean = condicionesParaAbrir.exists(condicion => condicion.apply(grupo))
  def condicionesParaAbrir : List[Condicion] = condicionBase :: this.condicionesEspecificas();
  def condicionesEspecificas() : List[Condicion] = ???
}

case class Abierta( val habitacionLadoA : Habitacion,val habitacionLadoB : Option[Habitacion]) extends Puerta(){
  override def condicionesEspecificas: List[Condicion] = ???
  override def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean = false
}

case class Cerrada( val habitacionLadoA : Habitacion,val habitacionLadoB : Option[Habitacion]) extends Puerta {//TODO: Aca hay que cambiar esto
  override def condicionesEspecificas() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.cofre.items.contains(Llave)
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase * estadoHeroe.heroe.nivel) >= 10
      case Ladrón(habilidadBase) => grupo.cofre.items.contains(Ganzúas)
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Escondida( val habitacionLadoA : Habitacion,val habitacionLadoB : Option[Habitacion]) extends Puerta{
  override def condicionesEspecificas() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case Ladrón(habilidadBase)  => (habilidadBase * estadoHeroe.heroe.nivel) >= 6
      case _ => false
    })
    val condicion2 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case  Mago(hechizosAprendibles)  => (Mago) (estadoHeroe.heroe.trabajo).conoceElHechizo(Vislumbrar,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1,condicion2)
  }
}

case class Encantada( val habitacionLadoA : Habitacion,val habitacionLadoB : Option[Habitacion], hechizoUtilizado: Hechizo) extends Puerta{
  override def condicionesEspecificas() : List[Condicion] = {
    val condicion1 : Condicion = (grupo) => grupo.exists(estadoHeroe => estadoHeroe.heroe.trabajo match {
      case  Mago(hechizosAprendibles)  => (Mago) (estadoHeroe.heroe.trabajo).conoceElHechizo(hechizoUtilizado,estadoHeroe.heroe.nivel);
      case _ => false
    })
    List(condicion1)
  }
}



case class Habitacion(val situacion : Situacion,val puertas : List[Puerta]){ //TODO: Terminar las habitaciones
  val agregarABotin : Function2[Item, Grupo[EstadoHeroe], Grupo[EstadoHeroe]] = (item,grupo) => {

  }
  def recorrerHabitacion(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] = {
    situacion match{
      case NoPasaNada() => grupo;
      case TesoroPerdido(item) => grupo.map(agregarABotin.apply(item,grupo));
      case MuchosMuchosDardos() => grupo.map(unEstadoHeroe => unEstadoHeroe.perderVida(10));
      case TrampaDeLeones() => grupo.map( h => h.matarCondicion(grupo.masLento()) );
      //case TrampaDeLeones() => grupo.agregarABotin(Llave);//Map no es del to-do util porque tenemos que analizar por to-do el conjunto
                              //Hacer un fold o un reduce para conseguir al mas lento. Primero habria que filtrar los vivos
                              //Dentro de la funcion del fold podria usarse para la comparacion que este vivo
      // case Encuentro(personalidad) => grupo //Map con aplicacion parcial para ver como se lleva con el lider?

      case Encuentro(heroeExtranjero : EstadoHeroe, grupo.getLider() ) =>  grupo.pelear(heroeExtranjero);
    }


  }
}

trait Situacion;
case class NoPasaNada() extends Situacion{
}
case class TesoroPerdido(val item: Item){
}
case class MuchosMuchosDardos(){

}
case class TrampaDeLeones(){

}

case class Encuentro(val personalidad : (Grupo[EstadoHeroe] => Boolean), val compatibilidad : Boolean = personalidad.apply(grupo));

