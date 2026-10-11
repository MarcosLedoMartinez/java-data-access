package com.marcosledo.accesodatos.xml;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Main {

    private static final Pattern patronEmail
            = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern patronNombre
            = Pattern.compile("^[\\p{L} .'-]{2,70}$");
    private static final Pattern patronTelefono
            = Pattern.compile("^\\+?\\d{7,15}$");

    public static void main(String[] args) {

        final String DIRECCIONXML = "data/clientes.xml";
        final String DIRECCIONLOG = "data/log.txt";
        int opcion;
        Registro log = new Registro(DIRECCIONLOG);
        GestorPersistenciaXML gestor = new GestorPersistenciaXML(DIRECCIONXML, log);
        ArrayList<Cliente> clientes = gestor.cargarClientes();
        Scanner sc = new Scanner(System.in);
        do {
            imprimirMenu();
            opcion = leerEntero(sc);
            System.out.println("");
            switch (opcion) {
                case 1:
                    listarClientes(clientes);
                    break;
                case 2:
                    AñadirClientes(clientes, sc, log);
                    break;
                case 3:
                    ModificarClientes(clientes, sc, log);
                    break;
                case 4:
                    EliminarClientes(clientes, sc, log);
                    break;
                case 5:
                    GuardarEnXML(gestor, clientes, sc);
                    break;
                case 6:
                    clientes = CargarListaDesdeXML(gestor, clientes, sc);
                    break;
                case 7:
                    EliminarListaEnXML(gestor, clientes, sc);
                    break;
                case 0:
                    System.out.println("SALIENDO...");
                    sc.close();
                    break;
                default:
                    System.out.println("OPCIÓN NO VALIDA, POR FAVOR, REPITA");
                    break;
            }
        } while (opcion != 0);

    }

    private static void imprimirMenu() {
        System.out.println("\n========================================");
        System.out.println("* MENU GESTION DE BASE DE DATOS DE CLIENTES* ");
        System.out.println("========================================\n");
        System.out.println("1 - LISTAR CLIENTES");
        System.out.println("2 - AÑADIR CLIENTES");
        System.out.println("3 - MODIFICAR CLIENTES");
        System.out.println("4 - ELIMINAR CLIENTES");
        System.out.println("5 - GUARDAR LISTA EN XML");
        System.out.println("6 - CARGAR LISTA DESDE XML");
        System.out.println("7 - ELIMINAR LISTA EN XML");
        System.out.println("0 - SALIR");
        System.out.print("\nPOR FAVOR, ELIJA UNA OPCION: ");
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

    private static void listarClientes(ArrayList<Cliente> clientes) {
        if (clientes.isEmpty()) {
            System.out.println("NO HAY CLIENTES CARGADOS EN LA LISTA");
        } else {
            for (Cliente c : clientes) {
                System.out.println(c.toString());
            }
        }
        System.out.println("\n========================================");

    }

    private static void AñadirClientes(ArrayList<Cliente> clientes, Scanner sc, Registro log) {
        boolean abortarAlta = true;
        boolean step;
        String nombre = "";
        String direccion = "";
        String telefono = "";
        String email = "";
        int indice = -1;
        boolean reemplazar = false;
        do {
            
            step = true;
            do {
                System.out.print("NOMBRE Y APELLIDOS DEL CLIENTE: ");
                nombre = sc.nextLine().trim();
                if (nombre.isEmpty()) {
                    System.out.println("EL NOMBRE NO PUEDE ESTAR VACIO!");
                } else if (!nombreValido(nombre)) {
                    System.out.println("EL NOMBRE INTRODUCIDO NO ES VALIDO!");
                } else {
                    step = false;
                }
            } while (step);
            
            step = true;
            do {
                System.out.print("DIRECCION DEL CLIENTE: ");
                direccion = sc.nextLine().trim();
                if (direccion.isEmpty()) {
                    System.out.println("LA DIRECCION NO PUEDE ESTAR VACIA!");
                } else {
                    step = false;
                }
            } while (step);
            
            step = true;
            do {
                System.out.print("TELEFONO DEL CLIENTE: ");
                telefono = sc.nextLine().trim();
                if (telefono.isEmpty()) {
                    System.out.println("EL TELEFONO NO PUEDE ESTAR VACIO!");
                } else if (!telefonoValido(telefono)) {
                    System.out.println("EL TELEFONO INTRODUCIDO NO ES VALIDO!");
                } else {
                    step = false;
                }
            } while (step);

            step = true;
            do {
                System.out.print("EMAIL DEL CLIENTE: ");
                email = sc.nextLine().trim();
                if (email.isEmpty()) {
                    System.out.println("EL EMAIL NO PUEDE ESTAR VACIO!");
                    continue;
                }
                if (!emailValido(email)) {
                    System.out.println("EL EMAIL INTRODUCIDO NO ES VALIDO!");
                    continue;
                }
                indice = verificarMail(clientes, email);
                reemplazar = false;
                if (indice >= 0) {
                    System.out.println("YA EXISTE UN CLIENTE CON ESE EMAIL:");
                    System.out.println(clientes.get(indice));
                    System.out.println("1 - MANTENER EL EXISTENTE (CANCELAR ALTA)");
                    System.out.println("2 - SUSTITUIR DATOS DEL EXISTENTE");
                    System.out.println("0 - INTRODUCIR OTRO EMAIL");
                    String opcionDup = sc.nextLine().trim();
                    if (opcionDup.equals("1")) {
                        log.registrar("alta", "fallido", "email duplicado: " + email);
                        return;
                    } else if (opcionDup.equals("2")) {
                        reemplazar = true;
                        step = false;
                    } else if (opcionDup.equals("0")) {
                        continue;
                    } else {
                        System.out.println("OPCION NO VALIDA.");
                    }
                } else {
                    step = false;
                }
            } while (step);

            System.out.println("\nSE VAN A INTRODUCIR LOS SIGUIENTES DATOS:");
            System.out.println("NOMBRE= " + nombre);
            System.out.println("DIRECCION= " + direccion);
            System.out.println("TELEFONO= " + telefono);
            System.out.println("MAIL= " + email);
            System.out.print("CONFIRMA? (S/N): ");

            String respuesta = sc.nextLine().trim();

            if (respuesta.equalsIgnoreCase("S")) {
                if (reemplazar && indice >= 0) {
                    Cliente c = clientes.get(indice);
                    c.setNombre(nombre);
                    c.setDireccion(direccion);
                    c.setTelefono(telefono);
                    c.setEmail(email);
                    log.registrar("modificacion", "exitoso", "reemplazo email: " + email);
                } else {
                    Cliente nuevo = new Cliente(nombre, direccion, telefono, email);
                    clientes.add(nuevo);
                    log.registrar("alta", "exitoso", "email: " + email);
                }
                abortarAlta = false;
            } else {
                log.registrar("alta", "fallido", "alta cancelada por usuario");
                abortarAlta = false;
            }
        } while (abortarAlta);
    }

    private static void ModificarClientes(ArrayList<Cliente> clientes, Scanner sc, Registro log) {
        if (clientes.isEmpty()) {
            System.out.println("NO HAY CLIENTES CARGADOS EN LA LISTA");
            return;
        }
        boolean step = true;
        int indice = -1;
        String email = "";
        do {
            System.out.print("EMAIL DEL CLIENTE A MODIFICAR: ");
            email = sc.nextLine().trim();
            if (email.isEmpty() || !emailValido(email)) {
                System.out.println("EMAIL NO VALIDO!");
                continue;
            }
            indice = verificarMail(clientes, email);
            if (indice < 0) {
                System.out.println("NO EXISTE NINGUN CLIENTE CON ESE EMAIL.");
                log.registrar("modificacion", "fallido", "no existe email: " + email);
                return;
            } else {
                step = false;
            }
        } while (step);

        Cliente actual = clientes.get(indice);
        System.out.println("DATOS ACTUALES:");
        System.out.println(actual);
        System.out.print("NUEVO NOMBRE (ENTER PARA MANTENER): ");
        String nuevoNombre = sc.nextLine().trim();
        if (!nuevoNombre.isEmpty() && nombreValido(nuevoNombre)) {
            actual.setNombre(nuevoNombre);
        }
        System.out.print("NUEVA DIRECCION (ENTER PARA MANTENER): ");
        String nuevaDireccion = sc.nextLine().trim();
        if (!nuevaDireccion.isEmpty()) {
            actual.setDireccion(nuevaDireccion);
        }
        System.out.print("NUEVO TELEFONO (ENTER PARA MANTENER): ");
        String nuevoTelefono = sc.nextLine().trim();
        if (!nuevoTelefono.isEmpty() && telefonoValido(nuevoTelefono)) {
            actual.setTelefono(nuevoTelefono);
        }
        log.registrar("modificacion", "exitoso", "email: " + actual.getEmail());
        System.out.println("CLIENTE MODIFICADO CORRECTAMENTE.");
    }

    private static void EliminarClientes(ArrayList<Cliente> clientes, Scanner sc, Registro log) {
        if (clientes.isEmpty()) {
            System.out.println("NO HAY CLIENTES CARGADOS EN LA LISTA");
            return;
        }
        System.out.print("EMAIL DEL CLIENTE A ELIMINAR: ");
        String email = sc.nextLine().trim();
        if (email.isEmpty() || !emailValido(email)) {
            System.out.println("EMAIL NO VALIDO!");
            return;
        }
        int indice = verificarMail(clientes, email);
        if (indice < 0) {
            System.out.println("NO EXISTE NINGUN CLIENTE CON ESE EMAIL.");
            log.registrar("eliminacion", "fallido", "no existe email: " + email);
            return;
        }

        Cliente clienteEliminar = clientes.get(indice);
        System.out.println("SE VA A ELIMINAR:");
        System.out.println(clienteEliminar);
        System.out.print("CONFIRMA? (S/N): ");
        String confirmar = sc.nextLine().trim();
        if (confirmar.equalsIgnoreCase("S")) {
            clientes.remove(indice);
            log.registrar("eliminacion", "exitoso", "email: " + email);
            System.out.println("CLIENTE ELIMINADO CORRECTAMENTE.");
        } else {
            log.registrar("eliminacion", "fallido", "eliminacion cancelada");
            System.out.println("ELIMINACION CANCELADA.");
        }
    }

    private static void GuardarEnXML(GestorPersistenciaXML gestor, ArrayList<Cliente> clientes, Scanner sc) {
        if (clientes.isEmpty()) {
            System.out.print("LA LISTA ESTA VACIA. ¿QUIERES GUARDAR UN XML VACIO? (S/N): ");
            String r = sc.nextLine().trim();
            if (!r.equalsIgnoreCase("S")) {
                System.out.println("GUARDADO CANCELADO.");
                return;
            }
        }
        gestor.guardarClientes(clientes);
        System.out.println("OPERACION DE GUARDADO FINALIZADA. REVISA EL LOG PARA VER EL RESULTADO.");
    }

    private static ArrayList<Cliente> CargarListaDesdeXML(GestorPersistenciaXML gestor, ArrayList<Cliente> clientes, Scanner sc) {
        if (!clientes.isEmpty()) {
            System.out.print("HAY CAMBIOS EN MEMORIA. ¿CARGAR Y PERDER LOS CAMBIOS NO GUARDADOS? (S/N): ");
            String r = sc.nextLine().trim();
            if (!r.equalsIgnoreCase("S")) {
                System.out.println("CARGA CANCELADA.");
                return clientes;
            }
        }
        ArrayList<Cliente> nuevaLista = gestor.cargarClientes();
        System.out.println("CARGA FINALIZADA. CLIENTES EN MEMORIA: " + nuevaLista.size());
        return nuevaLista;
    }
    
    private static void EliminarListaEnXML(GestorPersistenciaXML gestor, ArrayList<Cliente> clientes, Scanner sc) {
        System.out.print("¿SEGURO QUE QUIERES BORRAR EL FICHERO XML? (S/N): ");
        String r = sc.nextLine().trim();
        if (!r.equalsIgnoreCase("S")) {
            System.out.println("BORRADO CANCELADO.");
            return;
        }
        gestor.borrarXml();
        clientes.clear();
        System.out.println("BORRADO FINALIZADO. LISTA EN MEMORIA VACIADA.");
    }

    public static boolean emailValido(String email) {
        if (email == null) {
            return false;
        }
        email = email.trim();
        Matcher m = patronEmail.matcher(email);
        return m.matches();
    }

    public static boolean nombreValido(String nombre) {
        if (nombre == null) {
            return false;
        }
        nombre = nombre.trim();
        Matcher m = patronNombre.matcher(nombre);
        return m.matches();
    }

    public static boolean telefonoValido(String telefono) {
        if (telefono == null) {
            return false;
        }
        telefono = telefono.trim();
        Matcher m = patronTelefono.matcher(telefono);
        return m.matches();
    }

    public static int verificarMail(ArrayList<Cliente> clientes, String email) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getEmail().equalsIgnoreCase(email)) {
                return i;
            }
        }
        return -1;
    }
}
