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
      val heroe = Heroe(atributos, 2, 100, Ladrón(30), Loquitos,Heroico)

      //assert(heroe.trabajo.habilidadBase == 360)
    }
    /*"un mago cuando tiene el nivel para aprender un hechizo, adquiera dicho hechizo" in {

      var atributos = Atributos(100, 50, 80)
      val listahechizos = List("vislumbrar") //hay que cambiarlo cuando esten los hechizos
      var unmago = Mago(listahechizos)
      var introvertido = Introvertidos
      var heroe = Heroe(atributos, 1, 100, unmago, introvertido)

      //assert(heroe.trabajo.)
    }*/
    "Grupos" - {

    }

  }
}