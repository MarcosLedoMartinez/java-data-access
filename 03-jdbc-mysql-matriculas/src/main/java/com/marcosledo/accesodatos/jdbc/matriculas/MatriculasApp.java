package com.marcosledo.accesodatos.jdbc.matriculas;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class MatriculasApp {

    private static final int EDADMINIMA = 18;
    private static boolean abortarAlta;

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            if (!GestionBDD.probarConexion(
                    GestionBDD.getDatosConexion(),
                    GestionBDD.getUsuario(),
                    GestionBDD.getPassword()
            )) {
                System.out.println("\n******************************************************************\n");
                System.out.println("ERROR: NO SE PUDO CONECTAR A MYSQL.\n");
                System.out.println("REVISA: SERVICIO MYSQL ENCENDIDO, USUARIO/PASSWORD, Y PUERTO 3306.");
                System.out.println("\n******************************************************************\n");
                return;
            }
            System.out.println("\n===============================");
            System.out.println("CONEXION CON MYSQL ESTABLECIDA.");
            System.out.println("===============================\n");
            GestionBDD gestion = new GestionBDD();
            int opcion;
            do {
                imprimirMenu();
                opcion = leerEntero(sc);
                System.out.println("");
                switch (opcion) {
                    case 1:
                        listarCursos(gestion);
                        break;
                    case 2:
                        listarAlumnos(gestion);
                        break;
                    case 3:
                        insertarAlumno(sc, gestion);
                        break;
                    case 4:
                        modificarAlumno(sc, gestion);
                        break;
                    case 5:
                        eliminarAlumno(sc, gestion);
                        break;
                    case 0:
                        System.out.println("SALIENDO...");
                        break;
                    default:
                        System.out.println("OPCIÓN NO VALIDA, POR FAVOR, REPITA");
                        break;
                }
            } while (opcion != 0);
            gestion.cerrarConexion();
            System.out.println("\n================================================");
            System.out.println("PROGRAMA FINALIZADO. CONEXION CON MYSQL CERRADA.");
            System.out.println("================================================\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void imprimirMenu() {
        System.out.println("\n========================================");
        System.out.println("* MENU GESTION BASE DE DATOS FP LINKIA * ");
        System.out.println("========================================\n");
        System.out.println("1 - LISTAR CURSOS DISPONIBLES");
        System.out.println("2 - LISTAR ALUMNOS MATRICULADOS");
        System.out.println("3 - DAR DE ALTA A UN ALUMNO");
        System.out.println("4 - MODIFICAR ALUMNO");
        System.out.println("5 - DAR DE BAJA A UN ALUMNO");
        System.out.println("0 - SALIR");
        System.out.print("\nPOR FAVOR, ELIJA UNA OPCION: ");
    }

    private static void listarCursos(GestionBDD gestion) throws SQLException {
        ArrayList<Curso> cursos = gestion.listarCursos();
        if (cursos.isEmpty()) {
            System.out.println("NO HAY CURSOS EN LA BASE DE DATOS");
            return;
        }
        System.out.println("\n==========================================");
        System.out.println("LISTADO DE CURSOS DISPONIBLES EN FP LINKIA");
        System.out.println("==========================================\n");
        for (Curso c : cursos) {
            System.out.println(c);
        }
    }

    private static void listarAlumnos(GestionBDD gestion) throws SQLException {
        ArrayList<Alumno> alumnos = gestion.listarAlumnos();
        if (alumnos.isEmpty()) {
            System.out.println("NO CONSTA NINGUN ALUMNO EN LA BASE DE DATOS");
            return;
        }
        System.out.println("\n============================================");
        System.out.println("LISTADO DE ALUMNOS MATRICULADOS EN FP LINKIA");
        System.out.println("============================================\n");
        for (Alumno a : alumnos) {
            System.out.println(a);
        }
    }

    private static void insertarAlumno(Scanner sc, GestionBDD gestion) throws SQLException {
        abortarAlta = false;
        String nombre = pedirTextoObligatorio(sc, "NOMBRE");
        String apellidos = pedirTextoObligatorio(sc, "APELLIDOS");
        String email = pedirEmail(sc);
        if (abortarAlta) {
            System.out.println("\nALTA CANCELADA POR EL USUARIO.");
            return;
        }
        Date fechaNacimiento = pedirFechaNacimiento(sc);
        if (abortarAlta) {
            System.out.println("\nALTA CANCELADA POR EL USUARIO.");
            return;
        }
        int idCurso = pedirIdCurso(sc, gestion);
        String resumen
                = "NOMBRE: " + nombre + "\n"
                + "APELLIDOS: " + apellidos + "\n"
                + "EMAIL: " + emailMostrar(email) + "\n"
                + "FECHA NACIMIENTO: " + fechaMostrar(fechaNacimiento) + "\n"
                + "ID CURSO: " + idCurso;
        if (!confirmar(sc, resumen, "CONFIRMA EL ALTA DE ESTOS DATOS")) {
            System.out.println("\nALTA CANCELADA POR EL USUARIO.");
            return;
        }
        try {
            gestion.insertarAlumno(nombre, apellidos, email, fechaNacimiento, idCurso);
            System.out.println("\nALUMNO DADO DE ALTA CORRECTAMENTE.");
        } catch (SQLException e) {
            System.out.println("\nNO SE PUDO DAR DE ALTA AL ALUMNO (POSIBLE EMAIL YA EXISTENTE).");
        }
    }

    private static void modificarAlumno(Scanner sc, GestionBDD gestion) throws SQLException {
        abortarAlta = false;
        System.out.print("ID ALUMNO: ");
        int idAlumno = leerEntero(sc);
        Alumno actual = gestion.obtenerAlumnoPorId(idAlumno);
        if (actual == null) {
            System.out.println("NO EXISTE NINGUN ALUMNO CON ID " + idAlumno);
            return;
        }
        String nombreNuevo = pedirTextoMantener(sc, "NOMBRE", actual.getNombre());
        String apellidosNuevo = pedirTextoMantener(sc, "APELLIDOS", actual.getApellidos());
        String emailNuevo = pedirEmailMantener(sc, actual.getEmail());
        if (abortarAlta) {
            System.out.println("MODIFICACION CANCELADA POR EL USUARIO.");
            return;
        }
        Date fechaNueva = actual.getFecha_nacimiento();
        System.out.print("FECHA NACIMIENTO (EN BASE DE DATOS = " + fechaMostrar(fechaNueva) + ") NUEVA (DD/MM/AAAA) (ENTER = MANTENER BBDD): ");
        String fechaTxt = sc.nextLine().trim();

        if (!fechaTxt.isEmpty()) {
            Date candidata = Validaciones.fechaNacimientoASqlDate(fechaTxt, EDADMINIMA);
            while (candidata == null) {
                System.out.print("FECHA INVALIDA. INTRODUCE DE NUEVO (DD/MM/AAAA) O ENTER PARA MANTENER BBDD: ");
                fechaTxt = sc.nextLine().trim();
                if (fechaTxt.isEmpty()) {
                    candidata = fechaNueva;
                    break;
                }
                candidata = Validaciones.fechaNacimientoASqlDate(fechaTxt, EDADMINIMA);
            }
            fechaNueva = candidata;
        }

        int idCursoNuevo = actual.getId_curso();
        System.out.print("ID CURSO (EN BASE DE DATOS= " + idCursoNuevo + ") NUEVO CURSO (ENTER PARA MANTENER): ");
        String txtCurso = sc.nextLine().trim();
        if (!txtCurso.isEmpty()) {
            try {
                int candidato = Integer.parseInt(txtCurso);
                if (cursoExiste(candidato, gestion.listarCursos())) {
                    idCursoNuevo = candidato;
                } else {
                    System.out.println("ID CURSO NO EXISTE. SE MANTIENE EL ACTUAL.");
                }
            } catch (NumberFormatException e) {
                System.out.println("NO ES UN NUMERO. SE MANTIENE EL CURSO ACTUAL.");
            }
        }
        String resumen
                = "ID ALUMNO: " + idAlumno + "\n\n"
                + "NOMBRE: " + actual.getNombre() + "  ->  " + nombreNuevo + "\n"
                + "APELLIDOS: " + actual.getApellidos() + "  ->  " + apellidosNuevo + "\n"
                + "EMAIL: " + emailMostrar(actual.getEmail()) + "  ->  " + emailMostrar(emailNuevo) + "\n"
                + "FECHA NACIMIENTO: " + fechaMostrar(actual.getFecha_nacimiento()) + "  ->  " + fechaMostrar(fechaNueva) + "\n"
                + "ID CURSO: " + actual.getId_curso() + "  ->  " + idCursoNuevo;
        if (!confirmar(sc, resumen, "CONFIRMA LA MODIFICACION DE LOS DATOS?")) {
            System.out.println("\nMODIFICACION CANCELADA POR EL USUARIO.");
            return;
        }
        try {
            boolean ok = gestion.modificarAlumno(idAlumno, nombreNuevo, apellidosNuevo, emailNuevo, fechaNueva, idCursoNuevo);
            if (ok) {
                System.out.println("\nDATOS DEL ALUMNO MODIFICADOS CORRECTAMENTE.");
            } else {
                System.out.println("\nNO SE MODIFICO NINGUN REGISTRO.");
            }
        } catch (SQLException e) {
            System.out.println("\nNO SE PUDO MODIFICAR. POSIBLE EMAIL DUPLICADO O ERROR DE BBDD.");
        }
    }

    private static void eliminarAlumno(Scanner sc, GestionBDD gestion) throws SQLException {
        System.out.print("ID ALUMNO: ");
        int idAlumno = leerEntero(sc);
        Alumno alumno = gestion.obtenerAlumnoPorId(idAlumno);
        if (alumno == null) {
            System.out.println("\nNO EXISTE NINGUN ALUMNO CON ID " + idAlumno + ".");
            return;
        }
        String resumen
                = "ID ALUMNO: " + alumno.getId_alumno() + "\n"
                + "NOMBRE: " + alumno.getNombre() + "\n"
                + "APELLIDOS: " + alumno.getApellidos() + "\n"
                + "EMAIL: " + emailMostrar(alumno.getEmail()) + "\n"
                + "FECHA NACIMIENTO: " + fechaMostrar(alumno.getFecha_nacimiento()) + "\n"
                + "CURSO: " + alumno.getNombre_curso();
        if (!confirmar(sc, resumen, "DESEA CONFIRMAR LA BAJA DEL ALUMNO?")) {
            System.out.println("\nBAJA CANCELADA POR EL USUARIO.");
            return;
        }
        boolean ok = gestion.eliminarAlumno(idAlumno);
        if (ok) {
            System.out.println("\nALUMNO DADO DE BAJA CORRECTAMENTE.");
        } else {
            System.out.println("\nNO SE ELIMINO NINGUN REGISTRO.");
        }
    }

    private static String pedirEmail(Scanner sc) {
        abortarAlta = false;
        while (true) {
            System.out.print("EMAIL ALUMNO (ENTER PARA DEJAR VACIO, 0 PARA ABORTAR): ");
            String email = sc.nextLine().trim().toLowerCase();
            if (email.equals("0")) {
                abortarAlta = true;
                return null;
            }
            if (email.isEmpty()) {
                return null;
            }
            if (Validaciones.emailValido(email)) {
                return email;
            }
            System.out.println("\nEMAIL NO VALIDO. EJEMPLO CORRECTO: nombre.apellido@gmail.com .");
        }
    }

    private static Date pedirFechaNacimiento(Scanner sc) {
        while (true) {
            System.out.print("FECHA DE NACIMIENTO (DD/MM/AAAA): ");
            String fechaTexto = sc.nextLine().trim();
            if (fechaTexto.equals("0")) {
                abortarAlta = true;
                return null;
            }
            Date fecha = Validaciones.fechaNacimientoASqlDate(fechaTexto, EDADMINIMA);
            if (fecha != null) {
                return fecha;
            }
            System.out.println("\nFECHA INCORRECTA O BIEN NO CUMPLE CON LA EDAD MINIMA PARA SER MATRICULADO (" + EDADMINIMA + " AÑOS).");
            System.out.println("SI QUIERE ANULAR EL ALTA, TECLEE 0.\n");
        }
    }

    private static int pedirIdCurso(Scanner sc, GestionBDD gestion) throws SQLException {
        System.out.println("ELIGE ID CURSO:");
        ArrayList<Curso> cursos = gestion.listarCursos();
        for (Curso c : cursos) {
            System.out.println(c);
        }
        while (true) {
            System.out.print("ID CURSO: ");
            int idCurso = leerEntero(sc);
            if (cursoExiste(idCurso, cursos)) {
                return idCurso;
            }
            System.out.println("\nID DE CURSO NO EXISTENTE, ELIJE UNO DE LA LISTA, POR FAVOR.");
        }
    }

    private static boolean cursoExiste(int idCurso, ArrayList<Curso> cursos) {
        for (Curso c : cursos) {
            if (c.getId_curso() == idCurso) {
                return true;
            }
        }
        return false;
    }

    private static String pedirTextoMantener(Scanner sc, String etiqueta, String actual) {
        System.out.print(etiqueta + " (EN BASE DE DATOS= " + actual + ") NUEVO (ENTER = MANTENER): ");
        String nuevo = sc.nextLine().trim();
        return nuevo.isEmpty() ? actual : nuevo;
    }

    private static String pedirEmailMantener(Scanner sc, String actual) {
        while (true) {
            System.out.print("EMAIL (EN BASE DE DATOS= " + emailMostrar(actual) + ") NUEVO (ENTER = MANTENER, 0 = ABORTAR): ");
            String txt = sc.nextLine().trim();
            if (txt.equals("0")) {
                abortarAlta = true;
                return actual;
            }
            if (txt.isEmpty()) {
                return actual;
            }
            txt = txt.toLowerCase();
            if (Validaciones.emailValido(txt)) {
                return txt;
            }
            System.out.println("EMAIL NO VALIDO. EJEMPLO: nombre.apellido@gmail.com");
        }
    }

    private static boolean confirmar(Scanner sc, String resumen) {
        return confirmar(sc, resumen, "CONFIRMAR OPERACION");
    }

    private static boolean confirmar(Scanner sc, String resumen, String pregunta) {
        System.out.println("\n========================================");
        System.out.println("RESUMEN DE DATOS");
        System.out.println("========================================\n");
        System.out.println(resumen);

        while (true) {
            System.out.print("\n" + pregunta + " (S/N): ");
            String r = sc.nextLine().trim().toUpperCase();

            if (r.equals("S") || r.equals("SI")) {
                return true;
            }
            if (r.equals("N") || r.equals("NO")) {
                return false;
            }

            System.out.println("RESPUESTA NO VALIDA, POR FAVOR, ESCRIBE S O N.");
        }
    }

    private static int leerEntero(Scanner sc) {
        while (true) {
            String txt = sc.nextLine().trim();
            try {
                return Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                System.out.print("\nDEBES INTRODUCIR UN NUMERO ENTERO: ");
            }
        }
    }

    private static String emailMostrar(String email) {
        return (email == null) ? "(SIN EMAIL)" : email;
    }

    private static String fechaMostrar(Date fecha) {
        if (fecha == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new java.util.Date(fecha.getTime()));
    }

    private static String pedirTextoObligatorio(Scanner sc, String etiqueta) {
        while (true) {
            System.out.print(etiqueta + ": ");
            String txt = sc.nextLine().trim();
            if (!txt.isEmpty()) {
                return txt;
            }
            System.out.println("\"" + etiqueta + "\"" + " NO PUEDE ESTAR VACIO.\n");
        }
    }

}
