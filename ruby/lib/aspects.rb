class Aspects
  #attr_accessor :block
  @origen = ["Pepe"]

  def self.on(*argumentos,&bloque)
    #self.block = bloque
    #self.args = argumentos

    if argumentos == []
      raise ArgumentError.new
    else # si algun argumento pertenece a los de mi origen
      #argumentos.each { |a| puts a.to_s }

      argumentos.each do |arg|
        if @origen.any? { |o| (Kernel.const_get o) == arg }
          return "Exito!"
        end
      end

      raise ArgumentError.new # Origen vacio
    end
  end

end

class AspectsModificado

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

  end

end