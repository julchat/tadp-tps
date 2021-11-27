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
      var heroe = Heroe(atributos, 1, 100, Mago(hechizos), Introvertido)

      //assert(heroe.trabajo.conoceElHechizo)
    }
  }
    "Grupos" - {
      "si la salud de un aventurero baja a cero, es eliminado del grupo" in {
        var atributos = Atributos(100, 50, 80)
        var hechizos = List(HechizoAprendible(2, Vislumbrar))
        val ladron = Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico)
        val guerrerouno = Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico)
        val guerrerodos = Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico)
        val mago = Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico)

        var heroes=List(guerrerouno,ladron,guerrerodos,mago)

        var cofre=Cofre((Ganzúas,Llave),("chuchillo,pistola"),45)
        var grupo=GrupoVivo(heroes,cofre,Habitacion((NoPasaNada,TrampaDeLeones),Puerta((NoPasaNada,TesoroPerdido),(Cerrada,Escondida))))
        assert(grupo.cantidadDeVivos()==4)
      }
      
    }


}