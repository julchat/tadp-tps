require_relative 'origen'

class Aspects

  def self.on(*argumentos,&bloque)
    clases_modulos_encontrados = [] # lista que almacena (si es que existen) las clases/modulos/objetos pasados por parametro

    if argumentos.empty?
      raise ArgumentError.new('wrong number of arguments (0 for +1)')
    else # reviso si el argumento es una expresion regular
      argumentos.each do |arg|
        if arg.is_a? Regexp
          clases_modulos_encontrados.concat Object.constants.select { |constante| arg.match?(constante.to_s)}
        else
          clases_modulos_encontrados << arg
        end
      end

      raise ArgumentError.new("origen vacio") if clases_modulos_encontrados.empty?
    end

    @origenes = clases_modulos_encontrados.flat_map{ |un_origen| Origen.new(un_origen) }
  end

  def where(*condiciones)

  end

  def name(expresion_regular)

  end

  def transform(metodos,&bloque)

  end

  def redirect_to(objeto)

  end

end


# entorno
class ClaseA
  def saludar(x)
    "Hola, " + x
  end
end

class ClaseB
  def saludar(x)
    "Adiosín, " + x
  end
end

# test
Aspects.on ClaseA do
  #transform(where name(/saludar/)) do
  #  redirect_to(ClaseB.new)
  #end
  transform(ClaseA.instance_methods(false)) do
    redirect_to(ClaseB.new)
  end
end