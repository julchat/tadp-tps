package domain

class Calabozo(){
//TODO: Modelar como estarian las habitaciones y las puertas aca

}


trait Puerta{ //TODO: Desarrollar las puertas
  val primeraHabitacion : Habitacion
  val segundaHabitacion : Option[Habitacion]

  def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean = condicionesParaAbrir.exists(condicion => condicion(grupo))
  def condicionesParaAbrir : List[(Grupo[EstadoHeroe] => Boolean)]
}

case class Abierta() extends Puerta{

  override def puedoSerAbierta(grupo: Grupo[EstadoHeroe]): Boolean = false

}

case class Cerrada() extends Puerta {//TODO: Aca hay que cambiar esto
  override def condicionesParaAbrir : List[(Grupo[EstadoHeroe] => Boolean)]
  def condicion1 (grupo: Grupo) : (List[Boolean]) = {
    grupo.heroes.map(h => h.trabajo match
    {
      case Ladrón(habilidadBase) => habilidadBase * h.nivel >= 10
      case Ladrón(habilidadBase) => grupo.cofre.items.exists(item => item match {
        case Ganzúas => true
        case Llave => false
        case _ => false
      })
      case _ => grupo.cofre.items.contains(Llave)
    })

  }
}

case class Habitacion(val situacion : Situacion){ //TODO: Terminar las habitaciones
  def recorrerHabitacion(grupo: Grupo[EstadoHeroe]): Grupo[EstadoHeroe] ={
    situacion match{
      case NoPasaNada() => grupo;
      case TesoroPerdido(item) => grupo.agregarABotin(item);
      case MuchosMuchosDardos() => grupo.map(unEstadoHeroe => unEstadoHeroe.perderVida(10));
      case TrampaDeLeones() => grupo.map( h => h.matarCondicion(grupo.masLento()) );
      //case TrampaDeLeones() => grupo.agregarABotin(Llave);//Map no es del to-do util porque tenemos que analizar por to-do el conjunto
                              //Hacer un fold o un reduce para conseguir al mas lento. Primero habria que filtrar los vivos
                              //Dentro de la funcion del fold podria usarse para la comparacion que este vivo
      // case Encuentro(personalidad) => grupo //Map con aplicacion parcial para ver como se lleva con el lider?

      case Encuentro(heroeExtranjero : EstadoHeroe) =>  grupo.pelear(heroeExtranjero);
      case Encuentro(heroeExtranjero : EstadoHeroe) =>  grupo.pelear(heroeExtranjero);
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

case class Encuentro(val personalidad : (Grupo[EstadoHeroe] => Boolean));



