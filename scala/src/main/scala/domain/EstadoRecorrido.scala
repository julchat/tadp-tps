package domain
abstract class EstadoRecorrido(val grupo : Grupo){
  def recorrerHastaFallarOEncontrarSalida(puertaSalida: Puerta): EstadoRecorrido = {
    this.map(g => g.getLider().get.elegirPuerta(grupo) match {
      case Some(puerta) if puerta != puertaSalida => puerta.habitacion.recorrerHabitacion(g).estadoDelGrupo().recorrerHastaFallarOEncontrarSalida(puertaSalida)
      case Some(_) => RecorridoExitoso(grupo)
      case None => RecorridoFallidoPorPerdición(grupo)
    })
  }

  def map(funcion : Grupo => EstadoRecorrido) : EstadoRecorrido
}

case class RecorridoEnProceso(val _grupo : Grupo) extends EstadoRecorrido(_grupo){
  def map(funcion: Grupo => EstadoRecorrido): EstadoRecorrido = funcion.apply(grupo)
}

abstract class RecorridoFinalizado(val _grupo : Grupo) extends EstadoRecorrido(_grupo){
  def map(funcion: Grupo => EstadoRecorrido) : EstadoRecorrido = this
  }
case class RecorridoExitoso(val __grupo: Grupo) extends RecorridoFinalizado(__grupo)
case class RecorridoFallidoPorPerdición(val __grupo : Grupo) extends RecorridoFinalizado(__grupo)
case class RecorridoFallidoPorMuerte(val __grupo : Grupo) extends RecorridoFinalizado(__grupo)
