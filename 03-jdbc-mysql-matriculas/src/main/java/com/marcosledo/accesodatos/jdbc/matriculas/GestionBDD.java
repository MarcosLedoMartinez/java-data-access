package com.marcosledo.accesodatos.jdbc.matriculas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.Date;

public class GestionBDD {

    // Credenciales leídas de variables de entorno (nunca en el código fuente).
    // Valores por defecto pensados para un MySQL local de desarrollo.
    private static final String datos_conexion = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/");
    private static final String base_datos = "FPLinkia";
    private static final String usuario = System.getenv().getOrDefault("DB_USER", "root");
    private static final String password = System.getenv().getOrDefault("DB_PASSWORD", "");
    private static final int anyoCurso = 2025;
    private Connection con;

    public static String getDatosConexion() {
        return datos_conexion;
    }

    public static String getBaseDatos() {
        return base_datos;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean probarConexion(String url, String usuario, String password) {
        try (Connection c = DriverManager.getConnection(url, usuario, password)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public GestionBDD() throws SQLException {
        con = DriverManager.getConnection(datos_conexion, usuario, password);
        crearBdd();
        crearTablas();
        insertarCursosDefecto(anyoCurso);
    }

    private void crearBdd() throws SQLException {
        String query = "create database if not exists " + base_datos;

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
        con = DriverManager.getConnection(datos_conexion + base_datos, usuario, password);
    }

    private void crearTablas() throws SQLException {
        String crearCurso = "create table if not exists curso (id_curso int primary key auto_increment,"
                + " ciclo varchar (20) not null, curso int not null, anyo int not null);";
        String crearAlumno = "create table if not exists alumno (id_alumno int primary key auto_increment,"
                + " nombre varchar (50) not null, apellidos varchar (100) not null, email varchar (150) unique,"
                + " fecha_nacimiento date not null, id_curso int not null, constraint fk_alumno_curso foreign key (id_curso) references curso(id_curso));";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(crearCurso);
            stmt.executeUpdate(crearAlumno);
        }
    }

    private void insertarCursosDefecto(int anyoCurso) throws SQLException {
        String contarCursos = "select count(*) from curso";
        int total;
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(contarCursos)) {
            rs.next();
            total = rs.getInt(1);
        }
        if (total > 0) {
            return;
        }
        String insertarCursos = "insert into curso (ciclo, curso, anyo) values (?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(insertarCursos)) {
            String[] ciclos = {"DAM", "DAW", "ASIR"};
            for (String ciclo : ciclos) {
                ps.setString(1, ciclo);
                ps.setInt(2, 1);
                ps.setInt(3, anyoCurso);
                ps.executeUpdate();
                ps.setString(1, ciclo);
                ps.setInt(2, 2);
                ps.setInt(3, anyoCurso);
                ps.executeUpdate();

            }
        }
    }

    public ArrayList<Curso> listarCursos() throws SQLException {
        String peticion = "select id_curso, ciclo, curso, anyo from curso order by id_curso";
        ArrayList<Curso> cursos = new ArrayList<>();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(peticion)) {

            while (rs.next()) {
                cursos.add(new Curso(rs.getInt("id_curso"), rs.getString("ciclo"), rs.getInt("curso"), rs.getInt("anyo")));
            }
        }
        return cursos;
    }

    public void insertarAlumno(String nombre, String apellidos, String email,
            Date fecha_nacimiento, int id_curso) throws SQLException {
        String query = "insert into alumno (nombre, apellidos, email, fecha_nacimiento, id_curso) values (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            if (email == null) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, email);
            }
            ps.setDate(4, fecha_nacimiento);
            ps.setInt(5, id_curso);
            ps.executeUpdate();
        }
    }

    public boolean modificarAlumno(int idAlumno, String nombre, String apellidos, String email, 
            Date fechaNacimiento, int idCurso) throws SQLException {

        String query = "update alumno set nombre = ?, apellidos = ?, email = ?, fecha_nacimiento = ?, id_curso = ? where id_alumno = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nombre);
            ps.setString(2, apellidos);

            if (email == null) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, email);
            }

            ps.setDate(4, fechaNacimiento);
            ps.setInt(5, idCurso);
            ps.setInt(6, idAlumno);

            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }

    public boolean eliminarAlumno(int id_alumno) throws SQLException {
        String query = "delete from alumno where id_alumno = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id_alumno);
            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }

    public ArrayList<Alumno> listarAlumnos() throws SQLException {
        String query
                = "select a.id_alumno, a.nombre, a.apellidos, a.email, a.fecha_nacimiento, a.id_curso, "
                + "c.ciclo, c.curso, c.anyo "
                + "from alumno a "
                + "join curso c on a.id_curso = c.id_curso "
                + "order by a.id_alumno";

        ArrayList<Alumno> alumnos = new ArrayList<>();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String nombreCurso = construirNombreCurso(rs);
                alumnos.add(new Alumno(
                        rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getInt("id_curso"),
                        nombreCurso
                ));
            }
        }
        return alumnos;
    }

    public Alumno obtenerAlumnoPorId(int idAlumno) throws SQLException {
        String query
                = "select a.id_alumno, a.nombre, a.apellidos, a.email, a.fecha_nacimiento, a.id_curso, "
                + "c.ciclo, c.curso, c.anyo "
                + "from alumno a "
                + "join curso c on a.id_curso = c.id_curso "
                + "where a.id_alumno = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String nombreCurso = construirNombreCurso(rs);
                return new Alumno(
                        rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getInt("id_curso"),
                        nombreCurso
                );
            }
        }
    }

    private String construirNombreCurso(ResultSet rs) throws SQLException {
        String cursoTexto = (rs.getInt("curso") == 1) ? "1º CURSO" : "2º CURSO";
        return rs.getString("ciclo") + " " + cursoTexto + " (" + rs.getInt("anyo") + ")";
    }

    public void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
