class Pepe
  def saludar
    puts "HOla"
  end
end
class Juan

end
module Jugar
  def saludar

  end
end
pepito = Pepe.new
puts lista =  ObjectSpace.each_object(Module).select { |klass| klass.is_a? Module }
#puts lista_boba = ObjectSpace.each_object(Object).select {|a| a}
puts '-'*15
puts lista.include?(Jugar)
puts lista.include?(Juan)
puts lista.include?(Jugar)

