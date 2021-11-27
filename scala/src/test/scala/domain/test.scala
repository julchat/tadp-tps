package domain

import org.scalatest.freespec.AnyFreeSpec


class test extends AnyFreeSpec {

  "Trabajos" - {
    "un heroe con 100 de fuerza base y nivel 1 siendo guerrero debe tener 120 de fuerza" in {

      val atributo = Atributos(100, 50, 80)
      val unGuerrero = Heroe(atributo, 1, 100, Guerrero, Introvertido, Heroico)
      assert(unGuerrero.getFuerza() == 120)
    }
    "un ladron con habilidad base 30 y nivel 2 debe tener habilidad final 36" in {

      val atributos = Atributos(100, 50, 80)
      val heroe = Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico)

      //assert(heroe.trabajo.habilidadBase == 36)
    }
    "un mago cuando tiene el nivel para aprender un hechizo, adquiera dicho hechizo" in {

      var atributos = Atributos(100, 50, 80)
      var hechizos = List(HechizoAprendible(2, Vislumbrar))
      var heroe = Heroe(atributos, 1, 100, Mago(hechizos), Introvertido, Heroico)

      //assert(heroe.trabajo.conoceElHechizo)
    }
  }

  "Grupos" - {
    "si la salud de un aventurero baja a cero, es eliminado del grupo" in {
      var atributos = Atributos(100, 50, 80)
      var hechizos = List(HechizoAprendible(2, Vislumbrar))
      val ladron = Vivo(Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico))
      val guerrerouno = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
      val guerrerodos = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
      val mago = Vivo(Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico))

      var heroes=List[EstadoHeroe](guerrerouno,ladron,guerrerodos,mago)

      var cofre=Cofre(List(Ganzúas,Llave),List("chuchillo","pistola"),45)
      //var grupo=GrupoVivo(heroes,cofre,Habitacion((NoPasaNada,TrampaDeLeones),Puerta((NoPasaNada,TesoroPerdido),(Cerrada,Escondida))))
      var grupo=GrupoVivo[EstadoHeroe](heroes,cofre,Habitacion(NoPasaNada,List.empty),List.empty)

      assert(grupo.cantidadDeVivos()==4)
    }

    "si una de las habitaciones tiene trampa de leones, muere el mas lento" in {
      var atributos = Atributos(100, 50, 80)
      var atributosMasLento = Atributos(100, 5, 80)
      var hechizos = List(HechizoAprendible(2, Vislumbrar))
      val ladron = Vivo(Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico))
      val guerrerouno = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
      val guerrerodos = Vivo(Heroe(atributosMasLento, 2, 100, Guerrero, Loquitos, Heroico))
      val mago = Vivo(Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico))

      var heroes=List[EstadoHeroe](guerrerouno,ladron,guerrerodos,mago)

      var cofre=Cofre(List(Ganzúas,Llave),List("chuchillo","pistola"),45)
      //var grupo=GrupoVivo(heroes,cofre,Habitacion((NoPasaNada,TrampaDeLeones),Puerta((NoPasaNada,TesoroPerdido),(Cerrada,Escondida))))
      var grupo=GrupoVivo[EstadoHeroe](heroes,cofre,Habitacion(NoPasaNada,List.empty),List.empty)

      val calabozo = new Calabozo(Puerta(Some(Habitacion(TrampaDeLeones,List.empty)),List.empty),Puerta(None,List.empty))

      assert(calabozo.recorrer(grupo).cantidadDeVivos() == 3)
    }

  }

  "Calabozo" - {
    val habitacionA : Habitacion()
    val habitacionB : Habitacion()
    val habitacionC : Habitacion()
    val habitacionD : Habitacion()
    val habitacionE : Habitacion()
    val habitacionF : Habitacion()
    val habitacionG : Habitacion()
    val habitacionH : Habitacion()
    val habitacionI : Habitacion()
    val habitacionJ : Habitacion()
    val habitacionK : Habitacion()

    val puertaEntrada : Puerta = Puerta(Some(habitacionA))
    val puertaAB: Puerta = Puerta(Some(habitacionB))
    val puertaBC: Puerta = Puerta(Some(habitacionC))
    val puertaCD: Puerta = Puerta(Some(habitacionD))
    val puertaDE: Puerta = Puerta(Some(habitacionE))
    val puertaEF: Puerta = Puerta(Some(habitacionF))
    val puertaFG: Puerta = Puerta(Some(habitacionG))
    val puertaGK: Puerta = Puerta(Some(habitacionK))
    val puertaGH: Puerta = Puerta(Some(habitacion))
    val puertaHI: Puerta
    val puertaKI: Puerta
    val puertaIJ: Puerta
    val puertaJC: Puerta

  }

}