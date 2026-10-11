package com.marcosledo.accesodatos.basex;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class BaseXDemo {

    public static void main(String[] args) {
        GestorBaseX gestor = new GestorBaseX();
        String separador = "=".repeat(60);
        String xml = "<autores> <autor id=\"1\">"
                + "<nombre>Ana Pérez</nombre>"
                + "<nacionalidad>España</nacionalidad>"
                + "</autor> <autor id=\"2\"> <nombre>John Smith</nombre>"
                + "<nacionalidad>Reino Unido</nacionalidad>"
                + "</autor> </autores>";
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Bienvenido al desarrollo de la Actividad 2 de la\nUT5 de la asignatura de 1º de DAM Acceso A Datos");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos a conectar con la Base de Datos BaseX");
        System.out.println("Abrimos una sesión cliente con la API ClientSession");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.conectarBaseX();
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Vamos a crear la base de datos AutoresDB con los datos del XML aportado");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.ejecutarComando("CREATE DATABASE AutoresDB " + xml);
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Y, una vez creada, hacemos una lectura de la misma");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println(gestor.ejecutarConsulta("/autores/autor[nacionalidad=\"España\"]/nombre"));
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Ahora vamos a añadir <premios>2</premios> dentro");
        System.out.println("del autor con id=\"1\", con XQUERY Update.");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Capturamos el estado del autor con id=1 ANTES de añadir premios");
        System.out.println();
        String consultaAntes = gestor.ejecutarConsulta("/autores/autor[@id=\"1\"]");
        System.out.println();
        System.out.println("Añadimos el nodo premios al autor con id=1");
        System.out.println();
        gestor.ejecutarConsulta("insert node <premios>2</premios> into //autor[@id='1']");
        System.out.println();
        System.out.println("Y capturamos el estado del autor con id=1 DESPUES del añadido");
        System.out.println();
        String consultaDespues = gestor.ejecutarConsulta("/autores/autor[@id=\"1\"]");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Ahora vamos a mostrar el estado del autor con id=1 ANTES");
        System.out.println();
        System.out.println(consultaAntes);
        System.out.println();
        System.out.println("Y el estado del autor con id=1 DESPUES del update");
        System.out.println();
        System.out.println(consultaDespues);
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("Por ultimo, vamos a cerrar la sesion con el servidor BaseX ");
        System.out.println();
        System.out.println(separador);
        System.out.println();
        gestor.cerrarSesion();
        System.out.println();
        System.out.println(separador);
        System.out.println();
        System.out.println("FIN DEL PROGRAMA!");
        System.out.println();
        System.out.println(separador);
        System.out.println();
    }
}
