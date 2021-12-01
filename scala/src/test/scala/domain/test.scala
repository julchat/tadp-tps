package domain

import org.scalatest.freespec.AnyFreeSpec


class test extends AnyFreeSpec {

  "Trabajos" - {
    "un heroe con 100 de fuerza base y nivel 1 siendo guerrero debe tener 120 de fuerza" in {

      val atributo = Atributos(100, 50)
      val unGuerrero = Heroe(atributo, 1, 100, Guerrero, Introvertido, Heroico)
      assert(unGuerrero.getFuerza() == 120)
    }
    "un ladron con habilidad base 30 y nivel 2 debe tener habilidad final 36" in {

      val atributos = Atributos(100, 50)
      val heroe = Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico)

      // [TODO] no seria mejor que hereden de Guerrero, embes de hacer esto? del pater
      assert(heroe.habilidadEnSusManos == 36)
    }
    "un mago cuando tiene el nivel para aprender un hechizo, adquiera dicho hechizo" in {

      var atributos = Atributos(100, 50)
      var hechizos = List(HechizoAprendible(2, Vislumbrar))
      var heroe = Heroe(atributos, 1, 100, Mago(hechizos), Introvertido, Heroico)

      // [TODO] ACA igual tipo tendria que hacer lo mismo que en la anterior
      //assert(heroe.trabajo.conoceElHechizo)
    }
  }

  "Grupos" - {
    "si la salud de un aventurero baja a cero, es eliminado del grupo" in {
     /* var atributos = Atributos(100, 50, 80)
      var hechizos = List(HechizoAprendible(2, Vislumbrar))
      val ladron = Vivo(Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico))
      val guerrerouno = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
      val guerrerodos = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
      val mago = Vivo(Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico))

      var heroes=List[EstadoHeroe](guerrerouno,ladron,guerrerodos,mago)

      var cofre=Cofre(List(Ganzúas,Llave),List("chuchillo","pistola"),45)
      //var grupo=GrupoVivo(heroes,cofre,Habitacion((NoPasaNada,TrampaDeLeones),Puerta((NoPasaNada,TesoroPerdido),(Cerrada,Escondida))))
      var grupo=GrupoVivo(heroes,cofre,Habitacion(NoPasaNada,List.empty),List.empty)

      assert(grupo.cantidadDeVivos()==4)
      */
    }

    "si una de las habitaciones tiene trampa de leones, muere el mas lento" in {
    /*  var atributos = Atributos(100, 50, 80)
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
      */
    }

  }

  "Calabozo - con grupo perdido" - {
   /* val heroeAEncontrarse: Vivo = Vivo(Heroe(Atributos(10,2,50),5,25,Mago(List(HechizoAprendible(15,Vislumbrar))),Bigotes,Heroico));

    val puertaCD: Puerta = Puerta(Habitacion/*D*/(MuchosMuchosDardos, List()),List(Escondida));
    val puertaFG: Puerta = Puerta(Habitacion/*G*/(TrampaDeLeones, List()),List(Escondida));
    val puertaEF: Puerta = Puerta(Habitacion/*F*/(TesoroPerdido(Ganzúas), List(puertaFG)),List(Encantada(Vislumbrar)));
    val puertaEH: Puerta = Puerta(Habitacion/*H*/(Encuentro(heroeAEncontrarse), List()),List(Escondida));
    val puertaBC: Puerta = Puerta(Habitacion/*C*/(Encuentro(heroeAEncontrarse), List(puertaCD)),List(Escondida));
    val puertaAE: Puerta = Puerta(Habitacion/*E*/(NoPasaNada, List(puertaEF,puertaEH)),List(Cerrada,Escondida));
    val puertaAB: Puerta = Puerta(Habitacion/*B*/(NoPasaNada, List(puertaBC)),List(Cerrada,Escondida));
    val puertaPrincipal: Puerta = Puerta(Habitacion/*A*/(NoPasaNada, List(puertaAB,puertaAE)),List(Escondida,Cerrada));

    val calabozoTenebroso: Calabozo = new Calabozo(puertaPrincipal,puertaCD);

    var atributos = Atributos(100, 50, 80)
    var atributosMasLento = Atributos(100, 5, 80)
    var hechizos = List(HechizoAprendible(2, Vislumbrar))
    val ladron = Vivo(Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico))
    val guerrerouno = Vivo(Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico))
    val guerrerodos = Vivo(Heroe(atributosMasLento, 2, 100, Guerrero, Loquitos, Heroico))
    val mago = Vivo(Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico))

    val heroicos: List[EstadoHeroe] = List(ladron,guerrerouno,guerrerodos,mago);
    var cofre=Cofre(List(Ganzúas,Llave),List("chuchillo","pistola"),45)

    val grupoFantastico: GrupoVivo = new GrupoVivo(heroicos,cofre,List(),null) ;

    val grupoDespDelRecorrido: Grupo = calabozoTenebroso.recorrerTodoElCalabozo(grupoFantastico);

    println(grupoDespDelRecorrido.puntaje() + " -> " + grupoDespDelRecorrido.cantidadDeVivos() + " -> " + grupoDespDelRecorrido.cantidadDeMuertos());
*/
  }

  "Calabozo con grupo exitoso" - {

    val puertaSalida: Puerta = Puerta(Habitacion/*B*/(NoPasaNada, List()),List(Cerrada));
    val puertaPrincipal: Puerta = Puerta(Habitacion/*A*/(NoPasaNada, List(puertaSalida)),List(Cerrada));

    val calabozoTenebroso: Calabozo = new Calabozo(puertaPrincipal,puertaSalida);

    var atributos = Atributos(100, 50)
    var atributosMasLento = Atributos(100, 5)
    var hechizos = List(HechizoAprendible(2, Vislumbrar))
    val ladron = Heroe(atributos, 2, 100, Ladrón(30), Loquitos, Heroico)
    val guerrerouno = Heroe(atributos, 2, 100, Guerrero, Loquitos, Heroico)
    val guerrerodos = Heroe(atributosMasLento, 2, 100, Guerrero, Loquitos, Heroico)
    val mago = Heroe(atributos, 2, 100, Mago(hechizos), Loquitos, Heroico)

    val heroicos: List[Heroe] = List(ladron,guerrerouno,guerrerodos,mago);
    var cofre=Cofre(List(Llave),List("chuchillo","pistola"),45)

    val grupoFantastico: Grupo = new Grupo(heroicos,cofre,List(),null) ;

    //val grupoDespDelRecorrido: Grupo = calabozoTenebroso.recorrerTodoElCalabozo(grupoFantastico);

    //print(grupoDespDelRecorrido.puntaje() + " -> " + grupoDespDelRecorrido.cantidadDeVivos() + " -> " + grupoDespDelRecorrido.cantidadDeMuertos());

  }

  "Calabozo con grupo muerto" in {

    val puertaSalida: Puerta = Puerta(Habitacion/*B*/(NoPasaNada, List()),List(Cerrada));
    val atributosExtranjero = Atributos(300,50)
    val asesino = Heroe(atributosExtranjero,2,100,Guerrero,Loquitos,Vidente)
    val puertaPrincipal: Puerta = Puerta(Habitacion/*A*/(Encuentro(asesino), List(puertaSalida)),List(Cerrada));
    val calabozoTenebroso: Calabozo = new Calabozo(puertaPrincipal,puertaSalida);
    var atributos = Atributos(5, 50)
    var atributosMasLento = Atributos(5, 5)
    var hechizos = List(HechizoAprendible(2, Vislumbrar))
    val ladron = Heroe(atributos, 2, 5, Ladrón(30), Loquitos, Heroico)
    val guerrerouno = Heroe(atributos, 2, 5, Guerrero, Loquitos, Heroico)
    val guerrerodos = Heroe(atributosMasLento, 2, 5, Guerrero, Loquitos, Heroico)
    val mago = Heroe(atributos, 2, 5, Mago(hechizos), Loquitos, Heroico)



    val heroicos: List[Heroe] = List(ladron,guerrerouno,guerrerodos,mago);
    var cofre=Cofre(List(Llave),List("chuchillo","pistola"),45)

   /* val grupoFantastico: GrupoVivo = new GrupoVivo(heroicos,cofre,List(),null) ;

    val grupoDespDelRecorrido: Grupo = calabozoTenebroso.recorrerTodoElCalabozo(grupoFantastico);

   print(grupoDespDelRecorrido.puntaje() + " -> " + grupoDespDelRecorrido.cantidadDeVivos() + " -> " + grupoDespDelRecorrido.cantidadDeMuertos());
    assert(grupoDespDelRecorrido match {
      case gm : GrupoMuerto => true
      case _ => false
    })*/
  }

}