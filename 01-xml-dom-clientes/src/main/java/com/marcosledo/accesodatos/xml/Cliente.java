package com.marcosledo.accesodatos.xml;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class Cliente {

    private String nombre;
    private String direccion;
    private String telefono;
    private String email;

    public Cliente(String nombre, String direccion, String telefono, String email) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "NOMBRE: " + nombre + "\nDIRECCION: " + direccion + "\nTELEFONO: " + telefono + "\nEMAIL: " + email + "\n---------------------------------\n";
    }
}

