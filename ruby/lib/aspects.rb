module Name
  def name(regex)
    self.metodos.select{|un_metodo| regex.match?(un_metodo.to_s)}
  end
end

module Has_Parameters
  def has_parameters(*args)

  end
end

module Neg
  def neg(*args)

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

  def transform(*args, &bloque)

  end

end

class Origen
  attr_accessor :origen, :metodos
  include Name
  include Has_Parameters
  include Neg

  def initialize(origennuevo)
    metodos = Array.new
    self.origen = origennuevo
    origen = get_origen_posta
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
    puts args
    args.map { |una_condicion| self.send(una_condicion)}
    puts args.reduce(args.at(0)) { |listas_concatenadas, lista| lista & listas_concatenadas }
  end

  private def get_origen_posta
    if origen.is_a? Symbol
      origenposta = (Kernel.const_get (origen.to_s))
    else
      origenposta = origen
    end
  origenposta
end
end

class Pepe
  def ir_al_banio
    "Fui al banio"
  end

  def hacer_la_primera
    "hice la primera"
  end

  def hacer_la_segunda
    "hice la segunda"
  end
end

Aspects.on Pepe do
  where name(/hacer_la_primera/)
end


