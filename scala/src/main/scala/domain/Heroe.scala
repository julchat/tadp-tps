package domain

case class Heroe(val atributos : Atributos, val nivel : Int, val saludActual : Int ,
                 val trabajo : Trabajo,val compatibilidad : Compatibilidad, val criterioEleccion : Criterio){
  def getFuerza() : Int = {
    val adicional : Int =
      trabajo match {
        case Guerrero => (atributos.fuerzaBase * 0.2 * nivel).toInt
        case _ => 0
      }
    atributos.fuerzaBase + adicional
  }
  def vidaResultante (vidaAPerder : Int) : Int = math.max(0, saludActual - vidaAPerder);
  def bajarVida (vidaPerdida : Int ) : Heroe = this.copy(saludActual = saludActual - vidaPerdida);
  def subirNivel(nivelesGanados: Int) : Heroe = this.copy(nivel = nivel + nivelesGanados)

  def esLadron() : Boolean ={
    // [TODO] En todas las veces que usas Pater Machin para el "trabajo" solo lo usas para 1 comparacion
    trabajo match {
      case Ladrón(habilidadBase) => true
      case _ => false
    }
  }
  //atributo de la funcion para criterioEleccion a la cual le apliquemos el grupo
  //refactor para abstraer lo que esta en comun
  // este aunque sea tiene 3 case un poco distintos
  def elegirPuerta(grupo: Grupo):Grupo = criterioEleccion match {
    case Heroico => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.lastOption)
    case Ordenado => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.headOption)
    case Vidente => grupo.copy(puertaElegida = grupo.filtrarPuertasAbribles.sortBy(p => p.habitacion.recorrerHabitacion(grupo).puntaje()).lastOption)
  }

  def estoyVivo() : Boolean = {
    !(saludActual == 0);
  }
  // def matarCondicion(condicion: Heroe): Heroe;
  def getVelocidad() : Int = atributos.velocidadBase;


  def perderVida(vidaAPerder : Int) : Heroe = {
    if (vidaResultante(vidaAPerder) > 0){
      bajarVida(vidaAPerder);
    } else {
      bajarVida(saludActual)
    }
  }
  def habilidaEnSusManos():Int ={
    trabajo match {
      case Ladrón(habilidadBase) => habilidadBase + 3 * nivel
      case _ => 0
    }
  }

  /*  def matarCondicion(condicion: EstadoHeroe): EstadoHeroe ={
      if (this == condicion) {
        this.morir()
      }else
        this.copy()
    }*/



  // DONDE PONGO ESTO??
  /*  def esCompatible(grupo: GrupoVivo) : Boolean = {
      compatibilidad match{
        case introvertidos =>  grupo.length <= 3;
        case bigotes => // que le caen bien los grupos donde no hay ladrones
        case interesados(objParticular: Item) => // se suman a un grupo solamente si tiene cierto objeto  particular que le interesa.
        case loquitos => //siempre van a querer pelearse porque no les cae bien nadie
      }
    }*/
}

case class Atributos(val fuerzaBase : Int, val velocidadBase : Int)

trait Trabajo

case object Guerrero extends Trabajo
case class Ladrón(val habilidadBase : Int) extends Trabajo
case class Mago(val hechizosAprendibles : List[HechizoAprendible]) extends Trabajo{
  def conoceElHechizo(hechizo : Hechizo, nivelMago : Int) : Boolean = {
    hechizosAprendibles.exists(h => h.hechizo == hechizo && h.conoceElHechizo(nivelMago))
  }
}

case class HechizoAprendible(val nivelRequerido : Int, val hechizo : Hechizo){
  def conoceElHechizo(nivel : Int) : Boolean = {
    nivel >= nivelRequerido
  }
}

trait Hechizo
case object Vislumbrar extends Hechizo
