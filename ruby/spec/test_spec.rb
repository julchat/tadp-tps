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

end