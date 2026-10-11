package com.marcosledo.accesodatos.jdbc.matriculas;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Alumno {

    private int id_alumno;
    private String nombre;
    private String apellidos;
    private String email;
    private Date fecha_nacimiento;
    private int id_curso;
    private String nombre_curso;

    public Alumno(int id_alumno, String nombre, String apellidos, String email,
            Date fecha_nacimiento, int id_curso, String nombre_curso) {
        this.id_alumno = id_alumno;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.fecha_nacimiento = fecha_nacimiento;
        this.id_curso = id_curso;
        this.nombre_curso = nombre_curso;
    }

    public int getId_alumno() {
        return id_alumno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public int getId_curso() {
        return id_curso;
    }

    public String getNombre_curso() {
        return nombre_curso;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = (fecha_nacimiento != null) ? sdf.format(new Date(fecha_nacimiento.getTime())) : "";
        String emailTxt = (email == null) ? "(SIN EMAIL)" : email;

        return id_alumno + " - " + nombre + " " + apellidos
                + " | " + emailTxt
                + " | NAC: " + fecha
                + " | CURSO: " + nombre_curso;
    }
}
