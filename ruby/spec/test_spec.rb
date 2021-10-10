describe 'TEST AspectsModificado' do

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
  it 'probar inyeccion de parametros' do
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
    expect(instancia.hace_otra_cosa("foo", "foo")).to eq("bar-foo")
    # "bar:foo"
    expect(instancia.hace_algo("foo", "foo")).to eq("foo-bar")
    end
end

