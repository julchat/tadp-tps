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

    @origenes = clases_modulos_encontrados.map{ |un_origen| Origen.new(un_origen) }
    @origenes.flat_map{|origen| origen.filtrar_metodos}
  end

  def transform(*args, &bloque)

  end

  def where(*args, &bloque)

  end

end

class Origen
  attr_accessor :origen, :metodos

  def initialize(origennuevo)
    metodos = []
    self.origen = origennuevo
    (Kernel.const_get (origen.to_s)).singleton_class.define_method :get_origin_methods do
      if(self.instance_of? Class)
        puts "soy una clase o modulo"
        self.instance_methods
      else
        puts "soy un objeto"
        self.singleton_class.instance_methods
      end
    end
  end
  def filtrar_metodos
    (Kernel.const_get (origen.to_s)).send(:get_origin_methods)
  end
end

class Pepe
  def ir_al_banio
    "Fui al banio"
  end
end

=begin
puts Aspects.on /Pepe/ do

end
=end

miPepe = Pepe.new

puts Aspects.on miPepe do end