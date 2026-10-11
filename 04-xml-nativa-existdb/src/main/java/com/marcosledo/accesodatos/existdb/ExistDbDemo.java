package com.marcosledo.accesodatos.existdb;

import org.xmldb.api.base.Collection;

public class ExistDbDemo {

    public static void main(String[] args) {
        GestorConexion gestor = new GestorConexion();
        String separador = "=".repeat(60);
        String xml = "<biblioteca> \n"
                + "<libro> \n"
                + "<titulo>Acceso a Datos</titulo> \n"
                + "<autor>María López</autor> \n"
                + "<precio>29.95</precio> \n"
                + "</libro> \n"
                + "</biblioteca> ";
        
        System.out.println(separador);
        System.out.println();
        System.out.println("Bienvenido al desarrollo de la Actividad 1 de la\nUT5 de la asignatura de 1º de DAM Acceso A Datos");
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos a conectar con la Base de Datos eXist-db");
        System.out.println("Para ello vamos primero a inicializar el driver");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.registrarDriver();
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos a crear la coleccion Biblioteca, si no existe");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.crearColeccionSiNoExiste("Biblioteca");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Y, una vez creada, nos conectamos a ella...");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        Collection col = gestor.conectarAColeccion("Biblioteca");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Ahora vamos a insertar en Biblioteca el contenido");
        System.out.println("de libros.xml, que se recoge en la variable xml");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.insertarDocumento(col, "libros.xml", xml);
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Ahora vamos a modificar el documento, añadiendo un");
        System.out.println("nuevo nodo a libro <editorial>Ediciones DAM</editorial>");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.actualizarDocumento(col, "update insert <editorial>Ediciones DAM</editorial> into /biblioteca/libro");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos a consultar todos los titulos de los libros");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.leerDocumento(col, "for $libro in /biblioteca/libro return $libro/titulo");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos ahora a eliminar el documento libros.xml");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.eliminarDocumento(col, "libros.xml");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Y finalmente, cerramos la conexion a la BD");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.cerrarColeccion(col);
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("FIN DEL PROGRAMA!");
        System.out.println();
        System.out.println(separador);
        System.out.println();

    }
}
