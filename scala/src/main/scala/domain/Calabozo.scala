package domain

class ExecutionGrupoException(val grupo: Grupo) extends RuntimeException

case class Calabozo(puertaPrincipal: Puerta){
  def recorrer(grupo: Grupo): Grupo = {
    puertaPrincipal.abrir(grupo)
  }
}

case class Puerta(habitacion: Habitacion, obstaculo: Obstaculo){
  def abrir(grupo: Grupo): Grupo = {
    if (obstaculo(grupo)) habitacion.entrar(grupo)
    else throw new ExecutionGrupoException(grupo)
  }
}

trait Obstaculo{
  def apply(grupo: Grupo): Boolean = grupo.hayLadronConHabilidadOMas(grupo.heroes.map(heroe => heroe.trabajo),20) || sePuedeSuperar(grupo)
  def sePuedeSuperar(grupo: Grupo): Boolean
}

case object Cerrada extends Obstaculo {
  override def sePuedeSuperar(grupo: Grupo): Boolean = {
    grupo.cofre.contains(Llave) || grupo.cofre.contains(Ganzuas) || grupo.hayLadronConHabilidadOMas(grupo.heroes.map(heroe => heroe.trabajo),10)
  }
}

case object Escondida extends Obstaculo{
  override def sePuedeSuperar(grupo: Grupo): Boolean = {
    grupo.heroes.exists(heroe => {
      heroe.trabajo match {
        case Mago(hechizos) => hechizos.exists(hechizo => hechizo.conoceHechizo(Vislumbrar,heroe.nivel))
        case Ladron(habilidad) => habilidad >= 6
      }
    })
  }
}

case class Encantada(hechizoPuerta: Hechizo) extends Obstaculo{
  override def sePuedeSuperar(grupo: Grupo): Boolean = {
    grupo.heroes.exists(heroe => {
      heroe.trabajo match {
        case Mago(hechizos) => hechizos.exists(hechizo => hechizo.conoceHechizo(hechizoPuerta,heroe.nivel))
      }
    })
  }
}
//case object Abierta extends EstadoPuerta

case class Habitacion(situacion: Situacion, puertas: List[Puerta]){
  def entrar(grupo: Grupo): Grupo = {
    grupo.copy()
  }
}

trait Situacion
