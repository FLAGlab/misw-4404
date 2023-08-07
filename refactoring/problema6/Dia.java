package refactoring.problema6;

import java.util.ArrayList;

public class Dia {
    private int dia;
    private ArrayList<Evento> eventos;

    public Dia(int dia) {
        this.dia = dia;
        eventos = new ArrayList<Evento>();
    }

    public ArrayList<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(ArrayList<Evento> eventos) {
        this.eventos = eventos;
    }
}
