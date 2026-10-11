package com.marcosledo.accesodatos.jdbc.tienda;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Productos {

    private int id_producto;
    private String nombre;
    private int stock;

    public Productos(int id_producto, String nombre, int stock) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.stock = stock;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "ID PRODUCTO: " + id_producto + " NOMBRE: " + nombre + " STOCK: " + stock;
    }
}
