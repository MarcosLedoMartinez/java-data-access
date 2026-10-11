package com.marcosledo.accesodatos.jdbc.tienda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class GestorBBDD {

    // Credenciales leídas de variables de entorno (nunca en el código fuente).
    // Valores por defecto pensados para un MySQL local de desarrollo.
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/");
    private static final String USUARIO = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");
    private static final String BASEDATOS = "tiendaDB";
    private Connection con;

    public static String getURL() {
        return URL;
    }

    public static String getUSUARIO() {
        return USUARIO;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getBASEDATOS() {
        return BASEDATOS;
    }

    public static SQLException probarConexionServidor() {
        try (Connection con = DriverManager.getConnection(URL, USUARIO, PASSWORD)) {
            return null;
        } catch (SQLException e) {
            return e;
        }
    }

    public static SQLException crearBaseSiNoExiste() {
        try (Connection con = DriverManager.getConnection(URL, USUARIO, PASSWORD); Statement stmt = con.createStatement()) {

            String query = "CREATE DATABASE IF NOT EXISTS " + BASEDATOS;
            stmt.executeUpdate(query);

            return null;

        } catch (SQLException e) {
            return e;
        }
    }

    public static SQLException probarConexionBase() {
        try (Connection con = DriverManager.getConnection(URL + BASEDATOS, USUARIO, PASSWORD)) {
            return null;
        } catch (SQLException e) {
            return e;
        }
    }

    public GestorBBDD() throws SQLException {
        this.con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        crearBBDD();
        crearTablas();
        crearStockInicial();
    }

    private void crearBBDD() throws SQLException {
        String query = "CREATE DATABASE IF NOT EXISTS " + BASEDATOS;
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(query);
        }
        con = DriverManager.getConnection(URL + BASEDATOS, USUARIO, PASSWORD);
    }

    private void crearTablas() throws SQLException {
        String tabla1 = "CREATE TABLE IF NOT EXISTS productos ("
                + "id_producto INT PRIMARY KEY,"
                + "nombre VARCHAR (100), "
                + "stock INT);";
        String tabla2 = "CREATE TABLE IF NOT EXISTS ventas ("
                + "id_venta INT PRIMARY KEY,"
                + "id_producto INT, FOREIGN KEY (id_producto) REFERENCES productos (id_producto),"
                + "unidades INT,"
                + "fecha DATE);";
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(tabla1);
            stmt.executeUpdate(tabla2);
        }

    }

    private void crearStockInicial() throws SQLException {
        String comprobar = "SELECT COUNT(*) FROM productos";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(comprobar)) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return;
            }
        }
        String productos = "INSERT INTO productos (id_producto, nombre, stock) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(productos)) {
            ps.setInt(1, 1);
            ps.setString(2, "Teclado mecánico");
            ps.setInt(3, 50);
            ps.executeUpdate();
            ps.setInt(1, 2);
            ps.setString(2, "Ratón inalámbrico");
            ps.setInt(3, 30);
            ps.executeUpdate();
            ps.setInt(1, 3);
            ps.setString(2, "Monitor 24 pulgadas");
            ps.setInt(3, 20);
            ps.executeUpdate();
        }
    }

    public int realizarVenta(int idProducto, int unidades) throws SQLException {
        Date fecha = new Date(System.currentTimeMillis());
        String consultaStock = "SELECT stock FROM productos WHERE id_producto = ?";
        String insertarVenta = "INSERT INTO ventas (id_venta, id_producto, unidades, fecha) VALUES (?, ?, ?, ?)";
        String actualizarStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
        try {
            con.setAutoCommit(false);
            int stockActual;
            try (PreparedStatement ps = con.prepareStatement(consultaStock)) {
                ps.setInt(1, idProducto);
                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        con.rollback();
                        return 0;   
                    }
                    stockActual = rs.getInt("stock");
                }
            }
            if (unidades <= 0 || stockActual < unidades) {
                con.rollback();
                return -1;  
            }
            int idVenta = obtenerSiguienteIdVenta();
            try (PreparedStatement ps = con.prepareStatement(insertarVenta)) {
                ps.setInt(1, idVenta);
                ps.setInt(2, idProducto);
                ps.setInt(3, unidades);
                ps.setDate(4, fecha);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(actualizarStock)) {
                ps.setInt(1, unidades);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            }
            con.commit();
            return 1;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public ArrayList<Productos> listarProductos() throws SQLException {
        String query = "SELECT id_producto, nombre, stock FROM productos";
        ArrayList<Productos> productos = new ArrayList<>();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                productos.add(new Productos(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getInt("stock")
                ));
            }
        }
        return productos;
    }

    public ArrayList<Ventas> listarVentas() throws SQLException {
        String query = "SELECT id_venta, id_producto, unidades, fecha FROM ventas";
        ArrayList<Ventas> ventas = new ArrayList<>();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ventas.add(new Ventas(
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getInt("unidades"),
                        rs.getDate("fecha")
                ));
            }
        }

        return ventas;
    }

    private int obtenerSiguienteIdVenta() throws SQLException {
        String query = "SELECT COALESCE(MAX(id_venta), 0) + 1 AS siguiente FROM ventas";
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            return rs.getInt("siguiente");
        }
    }

    public boolean existeProducto(int idProducto) throws SQLException {
    String query = "SELECT 1 FROM productos WHERE id_producto = ?";
    try (PreparedStatement ps = con.prepareStatement(query)) {
        ps.setInt(1, idProducto);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}
    public void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println("================================================\n");
            System.out.println("ERROR AL CERRAR LA BASE DE DATOS!");
            System.out.println("DETALLE TÉCNICO: " + e.getMessage());
            System.out.println("================================================\n");
        }
    }

}
