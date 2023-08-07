import os

def todo_list():
    file_path = "./refactoring/problema4/data/todo.txt"

    try:
        file = open(file_path, 'r')
        lines = file.readlines()
        file.close()
    except FileNotFoundError:
        print('No existe el archivo todo.txt')
        lines = []
    
    lines = [line.rstrip() for line in lines]

    print('Bienvenido a su lista de tareas. Estas son sus opciones:')

    while True:
        print('-------------------------')
        print('1. Ver tareas')
        print('2. Agregar una tarea')
        print('3. Eliminar una tarea')
        print('4. Salir')
        print('-------------------------')

        opcion = input('Ingrese una opcion: ')

        if opcion == '1':
            print('-------------------------')
            print('Tareas:')
            for i, line in enumerate(lines):
                print('{}. {}'.format(i + 1, line))
        elif opcion == '2':
            tarea = input('Ingrese la tarea: ')
            lines.append(tarea)
            print('Tarea agregada')
        elif opcion == '3':
            tarea = input('Ingrese la tarea a eliminar: ')
            if tarea.isdigit() and (0 < int(tarea) <= len(lines)):
                lines.pop(int(tarea) - 1)
                print('Tarea eliminada')
            else: 
                print('No existe la tarea')
        elif opcion == '4':
            print('Adios')
            break

    file = open(file_path, 'w')
    for line in lines:
        file.write(line + '\n')
    file.close()
    

if __name__ == '__main__':
    todo_list()