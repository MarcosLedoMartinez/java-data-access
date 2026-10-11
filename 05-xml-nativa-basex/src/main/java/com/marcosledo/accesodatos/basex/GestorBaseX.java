package com.marcosledo.accesodatos.basex;

import org.basex.api.client.ClientSession;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class GestorBaseX {

    // Conexión configurable por variables de entorno (valores por defecto de un servidor BaseX local)
    final private String url = System.getenv().getOrDefault("BASEX_HOST", "localhost");
    final private int port = Integer.parseInt(System.getenv().getOrDefault("BASEX_PORT", "1984"));
    final private String user = System.getenv().getOrDefault("BASEX_USER", "admin");
    final private String password = System.getenv().getOrDefault("BASEX_PASSWORD", "admin");
    private ClientSession session = null;

    public void conectarBaseX() {
        try {
            this.session = new ClientSession(this.url, this.port, this.user, this.password);
            System.out.println("Conexion a BaseX realizada correctamente.");
            
        } catch (Exception e) {
            System.err.println("Error en la conexion a BaseX.\nDetalles del error: " + e.getMessage());
        }
    }

    public void ejecutarComando(String comando) {
        if (session != null) {
            try {
                session.execute(comando);
                System.out.println("Comando ejecutado correctamente.");
                
            } catch (Exception e) {
                System.err.println("Error en la ejecución: " + e.getMessage());
            }
        } else {
            System.out.println("No es posible ejecutar la orden\nNo hay conexion con la base de datos.");
        }
    }

    public String ejecutarConsulta(String consulta) {
        if (session != null) {
            try {
                String resultado = session.execute("XQUERY " + consulta);
                System.out.println("Consulta realizada correctamente.");
                return resultado;
            } catch (Exception e) {
                System.err.println("Error en la ejecución: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("No es posible realizar la consulta\nNo hay conexion con la base de datos.");
            return null;

        }
    }

    public void cerrarSesion() {
        if (session != null) try {
            session.close();
            System.out.println("La conexion con BaseX se ha cerrado correctamente.");

        } catch (Exception e) {
            System.err.println("No ha sido posible cerrar la conexion.\nInforme del error: " + e.getMessage());
        } else {
            System.out.println("No se ha detectado ninguna conexion que cerrar con BaseX.");
        }
    }
}
