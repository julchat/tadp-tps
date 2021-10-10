module Properties
  def mandatory = 1
  def optional = 2
end

module Name
  def name(*args)
   proc do |un_metodo|
      args.at(0).match?(un_metodo.to_s)
    end
  end
end

module Has_Parameters
  include Properties
  def has_parameters(*args)
     proc do |metodo, un_origen|
       parametros = un_origen.method(metodo).parameters
      if(args.at(1) == mandatory)
        parametros = parametros.select{ |un_parametro| un_parametro.at(0).to_s == "req"}
      end

      if(args.at(1) == optional)
        parametros = parametros.select{ |un_parametro| un_parametro.at(0).to_s == "opt"}
     end

      if(args.at(1).is_a? Regexp)
        parametros = parametros.select{ |un_parametro| args.at(1).match?(un_parametro.at(1).to_s)}
      end

      parametros.map { |un_parametro_par_ordenado| un_parametro_par_ordenado.at(1)}.size == args.at(0)
    end
  end
end

module Neg
  def neg(*args)
     proc do |un_metodo|
      args.none? do |una_condicion|
        una_condicion.call(un_metodo)
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

  def transform(*args, &bloque)

  end

end

class Origen
  attr_accessor :origen, :metodos
  include Name
  include Has_Parameters
  include Neg

  def initialize(origennuevo)
    self.metodos = Array.new
    self.origen = get_origen_posta origennuevo
    self.metodos = instance_exec origen do |origenaevaluar|
      if origenaevaluar.is_a? Class
        origenaevaluar.instance_methods
      else
        origenaevaluar.singleton_class.instance_methods
    end
    end
    self
  end

  def where(*args)
    if self.origen.is_a? Class
      origen_alterado = self.origen.new
    else
      origen_alterado = self.origen
    end
    puts self.metodos.select { |un_metodo| args.all?{ |una_condicion| una_condicion.call(un_metodo, origen_alterado)}}
  end

  private def get_origen_posta (origenposta)
    if origenposta.is_a? Symbol
      (Kernel.const_get (origenposta.to_s))
    else
      origenposta
    end
end
end