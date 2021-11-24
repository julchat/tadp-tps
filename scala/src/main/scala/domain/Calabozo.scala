package domain

case class Calabozo(puertaPrincipal: Puerta){
  def recorrer(grupo: Grupo): Grupo = puertaPrincipal.obstaculo match {
    case Cerrada => grupo.cofre.find()
  }
}

case class Puerta(habitacion: Habitacion, obstaculo: Obstaculo){

}

trait Obstaculo
case object Cerrada extends Obstaculo
case object Escondida extends Obstaculo
case object Encantada extends Obstaculo
//case object Abierta extends EstadoPuerta

case class Habitacion(situacion: Situacion, puertas: List[Puerta])

trait Situacion
