class Aspects
  def on(clase,modulo=nil,otro_modulo=nil)
    clase in [MiClase,OtraClase]
  end
end

class MiClase

end

class OtraClase

end


Aspects.on MiClase do
  # definición de aspectos para las instancias de MiClase
  def hello
    puts "Hola soy Mi Clase"
  end
end