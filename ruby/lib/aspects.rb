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

  def where(*args, &bloque)

  end

  def transform(*args, &bloque)

  end

end

class Origen
  attr_accessor :origen, :metodos

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


miPepe = Pepe.new

puts Aspects.on miPepe do end


