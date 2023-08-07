def check_password_strength(password):
    if len(password) < 8:
        return "La contraseña es muy corta"
    
    #criterios
    has_uppercase = False
    has_lowercase = False
    has_number = False
    has_symbol = False

    for char in password:
        if char.isupper():
            has_uppercase = True
        elif char.islower():
            has_lowercase = True
        elif char.isdigit():
            has_number = True
        elif char in "!@#$%^&*()_+-=,./<>?;:[]{}\|":
            has_symbol = True

    if not has_uppercase:
        return "La contraseña debe tener una mayúscula"
    if not has_lowercase:
        return "La contraseña debe tener una minúscula"
    if not has_number:
        return "La contraseña debe tener un número"
    if not has_symbol:
        return "La contraseña debe tener un símbolo"
    
    return "La contraseña es segura"

if __name__ == "__main__":
    password = input("Ingrese la contraseña: ")
    print(check_password_strength(password))