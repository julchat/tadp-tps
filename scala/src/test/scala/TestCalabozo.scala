import domain.{Atributos, Aventurero, Guerrero}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper


class TestCalabozo extends AnyFreeSpec{
  "tests calabozo" - {
    "aventurero" - {
      "aventurero de prueba" in {
        val atributos = Atributos(10,10)
        val aventurero = Aventurero(atributos,1,1,Guerrero)

        aventurero.fuerza() shouldBe 20
      }
    }
  }
}
