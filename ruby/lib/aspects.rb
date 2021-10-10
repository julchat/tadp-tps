module Inject
  def inject(hash)
    hash do | clave,valor |
      @metodos_filtrados.parameters.each do
        |req,param|
        if hash.key? (param)
          case hash[:param]
          when Proc
            param=hash[:param].call
          else
            param=hash[:param]
          end
        end
      end
    end
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

class MiClase
  def hace_algo(p1, p2)
    p1 + '-' + p2
  end
  def hace_otra_cosa(p2, ppp)
    p2 + ':' + ppp
  end
end



class Aspects
  include Inject
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

  def where(*args, &bloque)

  end

  def transform(*args, &bloque)

  end

end

class Origen
  attr_accessor :origen, :metodos, :metodos_filtrados
  include Inject
  def initialize(origennuevo)
    metodos = Array.new
    self.origen = origennuevo
    origen = get_origen_posta
    metodos = instance_exec origen do
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

  def get_origen_posta
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
end

class Juan

end

puts Aspects.on /Pepe/, Juan do

end




