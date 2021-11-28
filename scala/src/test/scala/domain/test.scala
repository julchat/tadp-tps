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
    "el lider de un grupo es el primer miembro del grupo" in {
      var atributos = Atributos(100, 50, 80)
      var heroes = List(Guerrero, Mago, Ladrón)
      var grupo = GrupoVivo(heroes, Cofre, Habitacion((NoPasaNada, TrampaDeLeones), Puerta((NoPasaNada, TesoroPerdido), (Cerrada, Escondida))))

      assert(grupo.getLider() == Guerrero)
    }


  }
  "Calabozo y puertas" - {
    "si una puerta esta CERRADA cualquier heroe puede abrirla si tiene el item llave" in {
      val atributo = Atributos(100, 50, 80)
      var heroes = List(Guerrero, Mago)
      var cofre = Cofre((Llave), ("chuchillo,pistola"), 45)
      var calabozo=Calabozo(Puerta(),Puerta(Habitacion,Habitacion,(Cerrada)))
      var grupo = GrupoVivo(heroes, cofre, Habitacion((NoPasaNada, TrampaDeLeones), Puerta((NoPasaNada, TesoroPerdido), (Cerrada, Escondida))))
      assert(calabozo.puertaPrincipal.puedoSerAbierta(grupo))
    }
    "si una puerta esta CERRADA cualquier cerradura puede ser abierta por un ladron si tiene habilidad mayor  igual que 10" +
      "o si el grupo tiene el item ganzuas" in {
      val atributo = Atributos(100, 50, 80)
      var heroes = List(Guerrero, Mago, Ladrón(20))
      var cofre = Cofre((Ganzúas), ("chuchillo,pistola"), 45)
      var calabozo=Calabozo(Puerta(),Puerta(Habitacion,Habitacion,(Cerrada)))
      var grupo = GrupoVivo(heroes, cofre, Habitacion((NoPasaNada, TrampaDeLeones), Puerta((NoPasaNada, TesoroPerdido), (Cerrada, Escondida))))

      assert(calabozo.puertaPrincipal.puedoSerAbierta(grupo))
    }
    "si una puerta esta ESCONDIDA un mago puede encontrarla si conoce el hechizo vislumbrar" in {
      val atributo = Atributos(100, 50, 80)
      var heroes = List(Guerrero, Mago((Vislumbrar)))
      var cofre = Cofre((Llave), ("chuchillo,pistola"), 45)
      var calabozo=Calabozo(Puerta(),Puerta(Habitacion,Habitacion,(Escondida)))
      var grupo = GrupoVivo(heroes, cofre, Habitacion((NoPasaNada, TrampaDeLeones), Puerta((NoPasaNada, TesoroPerdido), (Cerrada, Escondida))))
      assert(calabozo.puertaSalida.puedoSerAbierta(grupo))
    }
    "si una puerta esta ESCONDIDA un ladron con 6 o mas de habilidad puede encontarla " in {
      val atributo = Atributos(100, 50, 80)
      var heroes = List(Guerrero, Ladrón(10))
      var cofre = Cofre((Llave), ("chuchillo,pistola"), 45)
      var calabozo=Calabozo(Puerta(),Puerta(Habitacion,Habitacion,(Escondida)))
      var grupo = GrupoVivo(heroes, cofre, Habitacion((NoPasaNada, TrampaDeLeones), Puerta((NoPasaNada, TesoroPerdido), (Cerrada, Escondida))))
      assert(calabozo.puertaSalida.puedoSerAbierta(grupo))


}