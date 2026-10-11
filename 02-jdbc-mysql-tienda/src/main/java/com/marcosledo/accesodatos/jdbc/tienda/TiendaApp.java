package com.marcosledo.accesodatos.jdbc.tienda;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class TiendaApp {

    public static void main(String[] args) {
        SQLException error = GestorBBDD.probarConexionServidor();
        if (error == null) {
            error = GestorBBDD.crearBaseSiNoExiste();
        }
        if (error == null) {
            error = GestorBBDD.probarConexionBase();
        }
        System.out.println("=".repeat(57));
        System.out.println("BIENVENIDO AL GESTOR REMOTO DE LA BASE DE DATOS TIENDADB");
        System.out.println("=".repeat(57));
        if (error != null) {
            System.out.println("\n" + "#".repeat(55) + "\n");
            System.out.println("#### ERROR:  NO SE PUDO CONECTAR LA BASE DE DATOS. #### \n");
            switch (error.getErrorCode()) {
                case 1045: {
                    System.out.println("########## USUARIO O CONTRASEÑA INCORRECTOS ###########\n");
                    break;
                }
                case 1049: {
                    System.out.println("############### BASE DE DATOS DESCONOCIDA #############\n");
                    break;
                }
                case 2003: {
                    System.out.println("############# NO SE PUEDE CONECTAR AL SERVIDOR ########\n");
                    break;
                }
                case 1044: {
                    System.out.println("########### ACCESO DENEGADO A LA BASE DE DATOS ########\n");
                    break;
                }
                default: {
                    System.out.println("#### HA OCURRIDO EL SIGUIENTE ERROR, VER DETALLES: ####\n");
                    System.out.println("#### MENSAJE: " + error.getMessage() + " ####");
                    System.out.println("#### ESTADO SQL: " + error.getSQLState() + " ####");
                    System.out.println("#### CÓDIGO DE ERROR: " + error.getErrorCode() + " ####");
                }
            }
            System.out.println("#".repeat(55) + "\n");
            return;
        } else {
            try (Scanner sc = new Scanner(System.in)) {
                System.out.println("\n===============================");
                System.out.println("CONEXION CON MYSQL ESTABLECIDA.");
                System.out.println("===============================");
                GestorBBDD gestor = new GestorBBDD();
                int opcion;
                do {
                    imprimirMenu();
                    opcion = leerEntero(sc);
                    System.out.println("");
                    switch (opcion) {
                        case 1:
                            listarProductos(gestor);
                            break;
                        case 2:
                            realizarVenta(sc, gestor);
                            break;
                        case 3:
                            listarVentas(gestor);
                            break;
                        case 0:
                            System.out.println("SALIENDO...");
                            break;
                        default:
                            System.out.println("OPCIÓN NO VALIDA, POR FAVOR, REPITA");
                            break;
                    }
                } while (opcion != 0);
                gestor.cerrarConexion();
                System.out.println("\n================================================");
                System.out.println("PROGRAMA FINALIZADO. CONEXION CON MYSQL CERRADA.");
                System.out.println("================================================");

            } catch (SQLException e) {
                System.out.println("================================================\n");
                System.out.println("ERROR EN LA BASE DE DATOS!");
                System.out.println("DETALLE TÉCNICO: " + e.getMessage());
                System.out.println("================================================\n");
            }
        }
    }

    private static void imprimirMenu() {
        System.out.println("\n==========================================");
        System.out.println("  * MENÚ GESTIÓN BASE DE DATOS TIENDADB *  ");
        System.out.println("==========================================\n");
        System.out.println("1 - LISTAR PRODUCTOS DISPONIBLES");
        System.out.println("2 - REALIZAR VENTA DE PRODUCTO");
        System.out.println("3 - MOSTRAR VENTAS REGISTRADAS");
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

    private static void listarProductos(GestorBBDD gestor) throws SQLException {
        ArrayList<Productos> productos = gestor.listarProductos();

        if (productos.isEmpty()) {
            System.out.println();
            System.out.println("NO HAY PRODUCTOS REGISTRADOS.");
            return;
        }

        System.out.println("#".repeat(55));
        System.out.println("PRODUCTOS DISPONIBLES");
        System.out.println("#".repeat(55));
        System.out.println();
        for (Productos p : productos) {
            System.out.println(p);
        }
    }

    private static void realizarVenta(Scanner sc, GestorBBDD gestor) throws SQLException {
        int resultado;
        System.out.println("#".repeat(55));
        System.out.println("REALIZAR VENTA");
        System.out.println("#".repeat(55));
        System.out.println();

        System.out.print("INTRODUZCA LA ID DEL PRODUCTO: ");
        int idProducto = leerEntero(sc);
        if (!gestor.existeProducto(idProducto)) {
            resultado = 0;
        } else {
            System.out.print("INTRODUZCA EL NÚMERO DE UNIDADES: ");
            int unidades = leerEntero(sc);
            resultado = gestor.realizarVenta(idProducto, unidades);
        }
        switch (resultado) {
            case 1:
                System.out.println();
                System.out.println("VENTA REALIZADA CORRECTAMENTE. TRANSACCIÓN COMPLETADA.");
                break;
            case 0:
                System.out.println();
                System.out.println("EL PRODUCTO INDICADO NO EXISTE. TRANSACCIÓN CANCELADA.");
                break;
            case -1:
                System.out.println();
                System.out.println("NO HAY STOCK SUFICIENTE O LAS UNIDADES INTRODUCIDAS");
                System.out.println("DEBEN SER MAYORES QUE CERO. TRANSACCIÓN CANCELADA.");
                break;
            default:
                System.out.println();
                System.out.println("SE HA PRODUCIDO UN RESULTADO INESPERADO.");
                break;
        }
    }

    private static void listarVentas(GestorBBDD gestor) throws SQLException {
        ArrayList<Ventas> ventas = gestor.listarVentas();

        if (ventas.isEmpty()) {
            System.out.println();
            System.out.println("NO HAY VENTAS REGISTRADAS.");
            return;
        }

        System.out.println("#".repeat(55));
        System.out.println("VENTAS REGISTRADAS");
        System.out.println("#".repeat(55));
        System.out.println();

        for (Ventas v : ventas) {
            System.out.println(v);
        }
    }
}
