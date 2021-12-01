package domain
abstract class EstadoRecorrido(val grupo : Grupo){
  def elegirPuertaYRecorrer(calabozo : Calabozo): EstadoRecorrido = {
    grupo.getLider().get.elegirPuerta(grupo) match {
      case Some(puerta) => puerta.habitacion.recorrerHabitacion(grupo).estadoDelGrupo(calabozo)
      case None => RecorridoFallidoPorPerdición(grupo)
    };
  }
  def recorrerTodoElCalabozo(calabozo: Calabozo): EstadoRecorrido
}

case class RecorridoExitoso(val _grupo: Grupo) extends EstadoRecorrido(_grupo){
  def recorrerTodoElCalabozo(calabozo:Calabozo): EstadoRecorrido = {
    elegirPuertaYRecorrer(calabozo).recorrerTodoElCalabozo(calabozo)
  }
}
case class RecorridoFallidoPorPerdición(val _grupo : Grupo) extends EstadoRecorrido(_grupo){
  def recorrerTodoElCalabozo(calabozo : Calabozo): EstadoRecorrido = {
    this
  }
}

case class RecorridoFallidoPorMuerte(val _grupo : Grupo) extends EstadoRecorrido(_grupo){
  def recorrerTodoElCalabozo(calabozo:Calabozo) : EstadoRecorrido = {
    this
  }
}
