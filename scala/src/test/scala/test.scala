import domain.{Atributos, Guerrero, Heroe, Introvertidos, Ladrón, Mago, Vislumbrar}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.freespec.AnyFreeSpec


class test extends AnyFreeSpec {

  "Trabajos" - {
    "un heroe con 100 de fuerza base y nivel 1 siendo guerrero debe tener 120 de fuerza" in {

      var atributo = Atributos(100, 50, 80)
      var unguerrero = Guerrero
      var unintrovertido= Introvertidos
      var unGuerrero = Heroe(atributo, 1, 100, unguerrero, unintrovertido)

      unGuerrero.getFuerza()
      assert(unGuerrero.atributos.fuerzaBase == 120)
    }
    "un ladron con habilidad base 30 y nivel 2 debe tener habilidad final 36" in {

      var atributos = Atributos(100, 50, 80)
      var unladron = Ladrón(30)
      var introvertido = Introvertidos
      var heroe = Heroe(atributos, 2, 100, unladron, introvertido)

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