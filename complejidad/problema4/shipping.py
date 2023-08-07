def calcular_costo_envio(peso, pais):
    costo = None
    if pais == "Argentina":
        if peso <= 5:
            costo = 100
        else:
            costo = 100 + (peso - 5) * 10
    elif pais == "Brasil":
        if peso <= 5:
            costo = 200
        else:
            costo = 200 + (peso - 5) * 20
    elif pais == "Chile":
        if peso <= 5:
            costo = 300
        else:
            costo = 300 + (peso - 5) * 30
    elif pais == "Uruguay":
        if peso <= 5:
            costo = 400
        else:
            costo = 400 + (peso - 5) * 40
    
    return costo

if __name__ == "__main__":
    peso = float(input("Ingrese el peso del paquete: "))
    pais = input("Ingrese el pais de destino: ")
    costo = calcular_costo_envio(peso, pais)
    if costo is None:
        print("El pais ingresado no es valido")
    else:
        print("El costo de envio es: ", costo)