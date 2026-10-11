package com.marcosledo.accesodatos.jdbc.tienda;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Ventas {

    private int id_venta;
    private int id_producto;
    private int unidades;
    private Date fecha;

    public Ventas(int id_venta, int id_producto, int unidades, Date fecha) {
        this.id_venta = id_venta;
        this.id_producto = id_producto;
        this.unidades = unidades;
        this.fecha = fecha;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        LocalDate fechaLocal = fecha.toLocalDate();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "ID VENTA: " + id_venta
                + " | PRODUCTO: " + id_producto
                + " | UNIDADES: " + unidades
                + " | FECHA: " + fechaLocal.format(formato);
    }

}
