module Name
  def name(*args)
    self.metodos.select{|un_metodo| args.at(0).match?(un_metodo.to_s)}
  end
end

module Has_Parameters
  def has_parameters(*args)
    puts self.origen.class
    origenaux = self.origen.new
    if not(args.at(1).is_a?Regexp)
      self.metodos.select{|un_metodo| origenaux.method(un_metodo).arity == args.at(0)}
    end
  end
end

module Neg
  def neg(*args)

  end
end

module RedirectTo
  def redirect_to(objeto)
    @metodos_filtrados.each do |metodo_filtrado|
      @origen.define_method(metodo_filtrado.to_sym) do |*args|
        objeto.send(metodo_filtrado.to_sym,*args)
      end
    end
  end
end

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
    @origenes.each { |un_origen| un_origen.instance_eval(&bloque) }

  end

end

class Origen
  attr_accessor :origen, :metodos, :metodos_filtrados
  include Name
  include Has_Parameters
  include Neg
  include RedirectTo

  def initialize(origennuevo)
    metodos = Array.new
    self.origen = get_origen_posta origennuevo
    self.metodos = instance_exec origen do
    |origenaevaluar|
      if origenaevaluar.is_a? Class
        origenaevaluar.instance_methods
      else
        origenaevaluar.singleton_class.instance_methods
      end
    end
    self
  end

  def where(*args)
    args.reduce(args.at(0)) { |listas_concatenadas, lista| lista & listas_concatenadas }
  end

  def transform(metodos_filtrados, &bloque)
    @metodos_filtrados = metodos_filtrados
    instance_eval(&bloque)
  end

  private def get_origen_posta (origenposta)
    if origenposta.is_a? Symbol
      (Kernel.const_get (origenposta.to_s))
    else
      origenposta
    end
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
  transform(where name(/saludar/)) do
    redirect_to(ClaseB.new)
  end
end

puts ClaseA.new.saludar("Mundo")
