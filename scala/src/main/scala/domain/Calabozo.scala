package domain

trait Puerta{
  val primeraHabitacion : Habitacion
  val segundaHabitacion : Option[Habitacion]

  def puedoSerAbierta(grupo: Grupo): Boolean = condicionesParaAbrir.exists(condicion => condicion(grupo))
  def condicionesParaAbrir : List[(Grupo => Boolean)]
}

case class Abierta() extends Puerta{

  override def puedoSerAbierta(grupo: Grupo): Boolean = false

}

case class Cerrada() extends Puerta {
  override def condicionesParaAbrir : List[(Grupo => Boolean)]
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

