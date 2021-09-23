class PClase
  def saludar
    puts "hola desde MiClase"
  end
end
class SClase
  def saludar
    puts "hola desde MiClase"
  end
end

class Aspects
  @origen = [PClase,SClase]
  def self.on(*arg)
    if arg == []
      raise "ArgumentError: wrong number of arguments (0 for +1)"
    end

    if arg.length() == 1
      puts "Tengo 1 parametro"
      clase = arg[0]
      if clase.is_a? Class
        puts "si es clase"
      else
        puts "no es clase"
      end
    end

    if arg.length() == 3
      puts "Tengo 3 parametros"
      # arg[0] validar que es una CLAss
      # arg[1] validar que es una Modulo
      # arg[2]
    end
  end
end

# TEST
Aspects.on PClase, Bloque do
end
