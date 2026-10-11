package com.marcosledo.accesodatos.existdb;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XQueryService;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class GestorConexion {

    // Conexión configurable por variables de entorno (valores por defecto de una instalación local de eXist-db)
    final private String host = System.getenv().getOrDefault("EXIST_URI", "xmldb:exist://localhost:8080/exist/xmlrpc");
    final private String usuario = System.getenv().getOrDefault("EXIST_USER", "admin");
    final private String password = System.getenv().getOrDefault("EXIST_PASSWORD", "");
    final private String raiz = "/db";
    private Collection col = null;

    public void registrarDriver() {
        try {
            Class<?> cl = Class.forName("org.exist.xmldb.DatabaseImpl");
            Database database = (Database) cl.getDeclaredConstructor().newInstance();
            DatabaseManager.registerDatabase(database);
            System.out.println("Driver de eXist-db registrado correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar el driver: " + e.getMessage());
        }
    }

    public Collection conectarAColeccion(String nombreColeccion) {
        try {
            String rutaNormalizada = normalizadorRuta(nombreColeccion);
            String url = host + rutaNormalizada;
            col = DatabaseManager.getCollection(url, usuario, password);
            if (col != null) {
                if (rutaNormalizada.equals(raiz)) {
                    System.out.println("Conexión establecida con la raiz de la base de datos: " + rutaNormalizada);
                } else {
                    System.out.println("Conexión establecida correctamente con la coleccion: " + rutaNormalizada);
                }
                return col;
            } else {
                System.out.println("Esta coleccion no se encuentra: " + rutaNormalizada);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error al conectar a la Base de Datos= " + e.getMessage());
            return null;
        }
    }

    public void crearColeccionSiNoExiste(String nombreColeccion) {
        Collection coleccionRaiz = null;
        Collection coleccionNueva = null;
        try {
            coleccionRaiz = conectarAColeccion(raiz);
            String rutaNormalizada = normalizadorRuta(nombreColeccion);
            coleccionNueva = conectarAColeccion(rutaNormalizada);

            if (coleccionNueva != null) {
                System.out.println("La colección ya existe: " + rutaNormalizada);
                return;
            }
            CollectionManagementService servicio
                    = (CollectionManagementService) coleccionRaiz.getService("CollectionManagementService", "1.0");

            servicio.createCollection(nombreColeccion);
            System.out.println("Colección creada correctamente: " + rutaNormalizada);
        } catch (Exception e) {
            System.err.println("Error al crear la colección: " + e.getMessage());
        } finally {
            try {
                if (coleccionNueva != null) {
                    coleccionNueva.close();
                }
                if (coleccionRaiz != null) {
                    coleccionRaiz.close();
                }
            } catch (Exception e) {
                System.err.println("Error al cerrar colecciones: " + e.getMessage());
            }
        }
    }

    public void insertarDocumento(Collection coleccion, String nombreDocumento, String contenidoXML) {
        try {
            XMLResource recurso = (XMLResource) coleccion.createResource(nombreDocumento, "XMLResource");
            recurso.setContent(contenidoXML);
            coleccion.storeResource(recurso);
            System.out.println("Documento insertado correctamente: " + nombreDocumento);
        } catch (XMLDBException e) {
            System.err.println("Error XML:DB [código: " + e.errorCode + " ]: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al insertar el documento: " + e.getMessage());
        }
    }

    public void actualizarDocumento(Collection coleccion, String consultaUpdate) {
        try {
            XQueryService servicio = (XQueryService) coleccion.getService("XQueryService", "1.0");
            servicio.query(consultaUpdate);
            System.out.println("Documento modificado correctamente.");
        } catch (XMLDBException e) {
            System.err.println("Error XML:DB [código: " + e.errorCode + " ]: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al modificar el documento: " + e.getMessage());
        }
    }

    public void leerDocumento(Collection coleccion, String consulta) {
        try {
            XQueryService servicio = (XQueryService) coleccion.getService("XQueryService", "1.0");
            servicio.setProperty("indent", "yes");
            ResourceSet resultados = servicio.query(consulta);
            ResourceIterator it = resultados.getIterator();
            System.out.println("Resultados de la consulta:");
            while (it.hasMoreResources()) {
                Resource r = it.nextResource();
                System.out.println(r.getContent());
            }
        } catch (XMLDBException e) {
            System.err.println("Error XML:DB [código: " + e.errorCode + " ]: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
        }
    }

    public void eliminarDocumento(Collection coleccion, String nombreDocumento) {
        try {
            Resource recurso = coleccion.getResource(nombreDocumento);
            if (recurso != null) {
                coleccion.removeResource(recurso);
                System.out.println("Documento eliminado: " + nombreDocumento);
            } else {
                System.out.println("El documento no existe: " + nombreDocumento);
            }
        } catch (XMLDBException e) {
            System.err.println("Error XML:DB [código: " + e.errorCode + " ]: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al eliminar el documento: " + e.getMessage());
        }
    }

    public void cerrarColeccion(Collection coleccion) {
        if (coleccion != null) {
            try {
                coleccion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (XMLDBException e) {
                System.err.println("Error XML:DB [código: " + e.errorCode + " ]: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al cerrar la colección: " + e.getMessage());
            }
        } else {
            System.out.println("No hay conexion que cerrar.");
        }
    }

    private String normalizadorRuta(String nombreColeccion) {
        if (nombreColeccion == null || nombreColeccion.isBlank()) {
            return raiz;
        }
        String ruta = nombreColeccion.trim();
        if (ruta.equals(raiz)) {
            return raiz;
        }
        if (ruta.startsWith(raiz + "/")) {
            return ruta;
        }
        if (ruta.startsWith("/")) {
            ruta = ruta.substring(1);
        }
        return raiz + "/" + ruta;
    }
}
