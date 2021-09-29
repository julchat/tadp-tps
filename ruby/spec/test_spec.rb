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