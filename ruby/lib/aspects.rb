module Name
  def name(*args)
    self.metodos.select{|un_metodo| args.at(0).match?(un_metodo.to_s)}
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

module Neg
  def neg(*args)

  end
end

module RedirectTo
  def redirect_to(objeto)
    @metodos_filtrados.each do |metodo_filtrado|
      redireccion = proc do |*args|
        objeto.send(metodo_filtrado.to_sym,*args)
      end
      if @origen.is_a? Class
        @origen.define_method(metodo_filtrado.to_sym,redireccion)
      else
        @origen.define_singleton_method(metodo_filtrado.to_sym,redireccion)
      end
    end
  end
end

module InyeccionLogica
  def before(&bloque)
    @metodos_filtrados.each do |metodo_filtrado|
      @origen.alias_method "#{metodo_filtrado}_super_before".to_sym, metodo_filtrado.to_sym
      @origen.define_method("#{metodo_filtrado}_before".to_sym) do |instancia,cont,*args|
        send("#{metodo_filtrado}_super_before".to_sym,*args)
      end
    end

    metodo = @origen.new.method(:m1_before)

    @metodos_filtrados.each do |metodo_filtrado|
      @origen.define_method("#{metodo_filtrado}".to_sym) do |*args|
        instance_exec(@origen,metodo,*args,&bloque)
        #bloque.call(@origen,metodo,*args)
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

end

class Origen
  attr_accessor :origen, :metodos, :metodos_filtrados
  include Name
  include Has_Parameters
  include Neg
  include RedirectTo
  include InyeccionLogica

  def initialize(origennuevo)
    metodos = Array.new
    self.origen = get_origen_posta origennuevo
    self.metodos = instance_exec origen do
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

  private def get_origen_posta (origenposta)
    if origenposta.is_a? Symbol
      (Kernel.const_get (origenposta.to_s))
    else
      origenposta
    end
  end
end


class MiClase
  attr_accessor :x
  def m1(x, y)
    x + y
  end
  def m2(x)
    @x = x
  end
  def m3(x)
    @x = x
  end
end

class MiOtraClase
  attr_accessor :x
  def m1(x, y)
    x - y
  end
end

Aspects.on MiClase do
  transform(where name(/m1/)) do
    before do |instance, cont, *args|
      @x = 10
      new_args = args.map{ |arg| arg * 10 }
      cont.call(self, nil, *new_args)
    end
  end
  #transform(where name(/m2/)) do
  #  after do |instance, *args|
  #    if @x > 100
  #      2 * @x
  #    else
  #      @x
  #    end
  #  end
  #end
  #transform(where name(/m3/)) do
  #  instead_of do |instance, *args|
  #    @x = 123
  #  end
  #end
end

instancia = MiClase.new
puts instancia.m1(1, 3)
# 30
puts instancia.x
# 10

#instancia = MiClase.new
#instancia.m2(10)
## 10
#instancia.m2(200)
## 400

#instancia = MiClase.new
#instancia.m3(10)
#instancia.x
## 123
