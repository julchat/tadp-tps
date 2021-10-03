
=begin
class Aspects

  @origen = ["Pepe","SClase"]
=end
  # KERNEL.algo
  # UnModulo, otroModulo : Modulo segun la def is_a? Module

=begin
  def validar_expresion_regular(name_clase)
    /^Foo.*/, /.*bar/
  end
=end

=begin
  def self.on(*arg,&bloque)
    if arg == []
      raise ArgumentError.new
    end

    if arg.length == 1
      puts "Tengo 1 parametro"
      entidad = arg[0]

      if  entidad.instance_of? Regexp  # is_a?
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

      if  entidad.instance_of? Class # < Module
        puts "Sos una Clase"
      end

    end

    if arg.length == 3
      puts "Tengo 3 parametros"
    end
  end
end
=end

# TEST
describe 'Prototyped objects' do
  it 'ArgumentError: wrong number of arguments (0 for +1)' do
    expect {Aspects.on {}}.to raise_error(ArgumentError.new('wrong number of arguments (0 for +1)'))
  end

  it 'ArgumentError: origen vacío' do
    expect {Aspects.on /jorge/ do
              end}.to raise_error(ArgumentError)
  end
  it 'Origen valido' do
    expect(Aspects.on (/^Pepe/) {} ).to be true
  end
  # Bloque {} -> raise_error
end
=begin
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
=end
=begin
describe 'TEST Aspects' do
  class CN
  end
  class CO
  end
  class Pepe # SI pertenece al origen
  end

  it 'Debe tener al menos 1 argumento' do
    # Los Procedimientos van con {} osea no estoy esperando algo como un return. si puedo capturar un raise
      expect{Aspects.on {}}.to raise_error(ArgumentError)
  end

  it 'No hay parte del origen' do
    expect{Aspects.on CN, CO ,{}}.to raise_error(ArgumentError)
  end

  it 'Pepe si pertenece al origen' do
    # Creo que cuando espero que algo retorne "()"
    expect(Aspects.on CN, Pepe, {}).to eq("Exito!")
  end
end
=end


=begin
class Origen
  attr_accessor :origen, :metodos

  def initialize(origennuevo)
    metodos = []
    self.origen = origennuevo
    origenposta = get_origen_posta
    origenposta.define_method :get_origin_methods do
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

  private

  def get_origen_posta
    if origen.is_a? Symbol
      puts "soy un simbolo"
      origenposta = (Kernel.const_get (origen.to_s)).singleton_class
    else
      puts "no soy un simbolo"
      origenposta = origen
    end
    origenposta
  end
end
=end