describe 'TEST Aspects' do

  it 'Debe tener al menos 1 argumento' do
    # Los Procedimientos van con {} osea no estoy esperando algo como un return. si puedo capturar un raise
    expect{Aspects.on {}}.to raise_error(ArgumentError)
  end

  it 'Pepe no pertenece al origen' do
    expect{Aspects.on /Pepe/ do
                end}.to raise_error(ArgumentError)
  end

  it 'Pepe existe pero Juan no, pero no hay excepcion' do
    class Pepe

    end

    expect(Aspects.on /Pepe/, /Juan/, {}).to eq("Exito")
  end

  it "Voy a operar sobre saludar, porque juancito tiene el metodo"do
    class Juan
      def saludar
        "hola"
      end
    end
    juancito = Juan.new

    expect(Aspects.on(Juan).include?(:saludar))
  end

  it "La ClaseA ejecuta el metodo saludar de la ClaseB" do
    class ClaseA
      def saludar(x)
        "Hola, " + x
      end
    end

    class ClaseB
      def saludar(x)
        "Adiosín, " + x
      end
    end

    Aspects.on ClaseA do
      transform(where name(/saludar/)) do
        redirect_to(ClaseB.new)
      end
    end

    expect(ClaseA.new.saludar("mundo")).to eq("Adiosín, mundo")
  end

  it 'probar inyeccion de parametros' do
    class MiClase
      def hace_algo(p1, p2)
        p1 + '-' + p2
      end
      def hace_otra_cosa(p2, ppp)
        p2 + ':' + ppp
      end
    end


    Aspects.on MiClase do
      transform(where has_parameters(2, /p2/)) do
        inject(p2: 'bar')
      end
    end

    instancia = MiClase.new
    #instancia.hace_algo("foo")
    # "foo-bar"

    # "foo-bar"

    expect(instancia.hace_algo("foo", "foo")).to eq("foo-bar")
    #expect(instancia.hace_otra_cosa("foo", "foo")).to eq("bar-foo")
    # "bar:foo"
    #expect(instancia.hace_algo("foo", "foo")).to eq("foo-bar")
  end

  it "Inyeccion Logica: BEFORE" do
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

    Aspects.on MiClase do
      transform(where name(/m1/)) do
        before do |instance, cont, *args|
          @x = 10
          new_args = args.map{ |arg| arg * 10 }
          cont.call(self, nil, *new_args)
        end
      end
    end

    instancia = MiClase.new
    expect(instancia.m1(1, 2)).to eq(30)
    expect(instancia.x).to eq(10)
  end

  it "Inyeccion Logica: AFTER" do
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

    Aspects.on MiClase do
      transform(where name(/m2/)) do
        after do |instance, *args|
          if @x > 100
            2 * @x
          else
            @x
          end
        end
      end
    end

    instancia = MiClase.new
    expect(instancia.m2(10)).to eq(10)
    expect(instancia.m2(200)).to eq(400)
  end

  it "Inyeccion Logica: INSTANCE OF" do
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

    Aspects.on MiClase do
      transform(where name(/m3/)) do
        instead_of do |instance, *args|
          @x = 123
        end
      end
    end

    instancia = MiClase.new
    instancia.m3(10)
    expect(instancia.x).to eq(123)
  end
end