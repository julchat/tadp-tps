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