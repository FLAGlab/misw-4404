def checkear_contrasena(contrasena):
    segura = True

    if len(contrasena) < 8:
        segura = False
    elif contrasena.isalpha():
        segura = False
    elif contrasena.isdigit():
        segura = False
    else:
        char_especial= ['!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '=']
        cuenta = 0
        for char in contrasena:
            if char in char_especial:
                cuenta += 1
        if cuenta < 2:
            segura = False

    return segura

if __name__ == '__main__':
    contrasena = input('Ingrese una contrasena: ')
    if checkear_contrasena(contrasena):
        print('Contrasena segura')
    else:
        print('Contrasena insegura')