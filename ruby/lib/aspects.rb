module Properties
  def mandatory
    1
  end
  def optional
    2
  end
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
      parametros_original = un_origen.method(metodo).parameters
      if(args.at(1) == mandatory)
        parametros = parametros.select{ |un_parametro| un_parametro.at(0).to_s == "req"}
      end

      if(args.at(1) == optional)
        parametros = parametros.select{ |un_parametro| un_parametro.at(0).to_s == "opt"}
      end

      if(args.at(1).is_a? Regexp)
        parametros = parametros.select{ |un_parametro| args.at(1).match?(un_parametro.at(1).to_s)}
      end

      if args.at(1).is_a? Regexp
        parametros.size > 0 and parametros_original.size == args.at(0)
      else
        parametros.map { |un_parametro_par_ordenado| un_parametro_par_ordenado.at(1)}.size == args.at(0)
      end
    end
  end
end

module Neg
  def neg(*args)
    proc do |un_metodo, origen|
      args.none? do |una_condicion|
        una_condicion.call(un_metodo, origen)
      end
    end
  end
end

module Inject
  def inject(un_hash)
    @metodos_filtrados.each do |metodo_filtrado|
      parametros_nuevos = []
      @origen.new.method(metodo_filtrado.to_sym).parameters.each do |parametro|

        parametro_nuevo = { es_nuevo: false, valor: nil}

        if un_hash.key? parametro[1]
          case un_hash[parametro[1]]
          when Proc
            param = un_hash[parametro[1]] #.call
          else
            param = un_hash[parametro[1]]
          end

          parametro_nuevo[:es_nuevo] = true
          parametro_nuevo[:valor] = param
        end

        parametros_nuevos.push(parametro_nuevo)
      end

      bloque_metodo = @origen.new.method(metodo_filtrado.to_sym).to_proc
      @origen.define_method(metodo_filtrado.to_sym) do |*args|
        if parametros_nuevos.size > args.size
          args.concat(Array.new(parametros_nuevos.size - args.size))
        end

        nuevo_args = args.map.with_index do |x,i|
          if parametros_nuevos[i][:es_nuevo]
            if parametros_nuevos[i][:valor].is_a? Proc
              parametros_nuevos[i][:valor].call(@origen,metodo_filtrado,x)
            else
              parametros_nuevos[i][:valor]
            end
          else
            x
          end
        end
        bloque_metodo.call(*nuevo_args)
      end
    end
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

      metodo = @origen.new.method("#{metodo_filtrado}_before".to_sym) # "algo".to_sym => :algo

      @origen.define_method("#{metodo_filtrado}".to_sym) do |*args|
        instance_exec(@origen,metodo,*args,&bloque) # Siempre el bloque al final
        #            (instance, cont, *args) correponden a los parametros del '&bloque'
      end
    end
  end

  def after(&bloque)
    @metodos_filtrados.each do |metodo_filtrado|
      @origen.alias_method "#{metodo_filtrado}_after".to_sym, metodo_filtrado.to_sym
      @origen.define_method("#{metodo_filtrado}".to_sym) do |*args|
        send("#{metodo_filtrado}_after".to_sym,*args)
        instance_exec(@origen,*args,&bloque) # Siempre el bloque al final
      end
    end
  end

  def instead_of(&bloque)
    @metodos_filtrados.each do |metodo_filtrado|
      @origen.alias_method "#{metodo_filtrado}_instead_of".to_sym, metodo_filtrado.to_sym
      @origen.define_method("#{metodo_filtrado}".to_sym) do |*args|
        instance_exec(@origen,*args,&bloque) # Siempre el bloque al final
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
  include Inject
  include RedirectTo
  include InyeccionLogica

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
    self.metodos.select { |un_metodo| args.all?{ |una_condicion| una_condicion.call(un_metodo, origen_alterado)}}
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
