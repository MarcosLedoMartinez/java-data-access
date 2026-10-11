package com.marcosledo.accesodatos.jdbc.matriculas;

public class Curso {

    private int id_curso;
    private String ciclo;
    private int curso;
    private int anyo;

    public Curso(int id_curso, String ciclo, int curso, int anyo) {
        this.id_curso = id_curso;
        this.ciclo = ciclo;
        this.curso = curso;
        this.anyo = anyo;
    }

    public int getId_curso() {
        return id_curso;
    }

    public String getCiclo() {
        return ciclo;
    }

    public int getCurso() {
        return curso;
    }

    public int getAnyo() {
        return anyo;
    }

    @Override
    public String toString() {
        String cursoTexto = (curso == 1) ? "1º CURSO" : "2º CURSO";
        return id_curso + " - " + ciclo + " " + cursoTexto + " (" + anyo + ")";
    }
}
