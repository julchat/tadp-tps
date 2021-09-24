class Aspects

  @origen = ["Pepe","SClase"]

=begin
  def validar_expresion_regular(name_clase)
    /^Foo.*/, /.*bar/
  end
=end

  def self.on(*arg,&bloque)
    if arg == []
      raise ArgumentError.new
    end

    if arg.length == 1
      puts "Tengo 1 parametro"
      entidad = arg[0]

      if  entidad.instance_of? Regexp
        puts "Sos una expresion regular"
        #@origen.include?(bloque)
        #entidad.match("Pepe y Juan")
        if entidad.match("Pepe y Juan")
          # La logica que debe pasar cuando se encuentre
          return true
        else
          return raise ArgumentError.new
        end

      end

      if  entidad.instance_of? Class
        puts "Sos una Clase"
      end
    end

    if arg.length == 3
      puts "Tengo 3 parametros"
    end
  end
end

# TEST
describe 'Prototyped objects' do
  it 'ArgumentError: wrong number of arguments (0 for +1)' do
    expect {Aspects.on  do
              end}.to raise_error(ArgumentError)
  end

  it 'ArgumentError: origen vacío' do
    expect {Aspects.on /jorge/ do
              end}.to raise_error(ArgumentError)
  end
  it 'Origen valido' do
    expect{Aspects.on /^Pepe/ do
            end}.to be(true)
  end
end

