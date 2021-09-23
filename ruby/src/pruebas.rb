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

    if arg.length == 1
      puts "Tengo 1 parametro, deberia ser un bloque"
      bloque = arg[0]

      if  bloque.instance_of? Regexp
        puts "Sos una expresion regular"
      end

      if  bloque.instance_of? Class
        puts "Sos una Clase"
      end
    end

    if arg.length == 3
      puts "Tengo 3 parametros"
      # arg[0] validar que es una CLAss
      # arg[1] validar que es una Modulo
      # arg[2]
    end
  end
end

# TEST
#Aspects.on /^Foo.*/ do
#end

Aspects.on  do
end
