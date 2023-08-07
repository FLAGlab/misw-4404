package refactoring.problema6;

import java.util.ArrayList;

public class Calendario {
    private Dia[] dias;

    public Calendario() {
        dias = new Dia[30];
    }

    public void setDias(Dia[] dias) {
        this.dias = dias;
    }

    public Dia[] getDias() {
        return dias;
    }

    public static void main(String[] args) {
        Calendario calendario = new Calendario();
        
        for (int i = 0; i < calendario.getDias().length; i++) {
            calendario.getDias()[i] = new Dia(i+1);
        }

        System.out.println("Bienvenido al calendario.");

        while (true) {
            System.out.println("-------------------------");
            System.out.println("1. Ver eventos");
            System.out.println("2. Agregar evento");
            System.out.println("3. Salir");
            System.out.println("Seleccione una opción:");
            
            int opcion = Integer.parseInt(System.console().readLine());
            System.out.println("-------------------------");

            if (opcion == 1) {
                System.out.println("Ingrese el día del que quiere ver los eventos:");
                int dia = Integer.parseInt(System.console().readLine());

                Dia diaSeleccionado = calendario.getDias()[dia-1];

                System.out.println("Eventos del día " + dia + ":");
                if (diaSeleccionado.getEventos().isEmpty()){
                    System.out.println("No hay eventos para este día.");
                } else {
                    for (Evento evento : diaSeleccionado.getEventos()) {
                        System.out.println(evento.getNombre());
                    }
                }
            } else if (opcion == 2) {
                System.out.println("Ingrese el día en el que quiere agregar un evento:");
                int dia = Integer.parseInt(System.console().readLine());

                Dia diaSeleccionado = calendario.getDias()[dia-1];

                System.out.println("Ingrese el nombre del evento:");
                String nombre = System.console().readLine();

                Evento evento = new Evento(nombre);
                diaSeleccionado.getEventos().add(evento);
            } else if (opcion == 3) {
                System.out.println("Gracias por usar el calendario.");
                break;
            } else {
                System.out.println("Opción inválida.");
            }
        }
    }
}
