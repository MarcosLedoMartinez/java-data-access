package com.marcosledo.accesodatos.xml;

import java.util.ArrayList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author Marcos Ledo Martinez
 */
public class GestorPersistenciaXML {

    private String rutaXml;
    private Registro registro;

    public GestorPersistenciaXML(String rutaXml, Registro registro) {
        this.rutaXml = rutaXml;
        this.registro = registro;
    }

    public ArrayList<Cliente> cargarClientes() {
        ArrayList<Cliente> listaClientes = new ArrayList<>();
        File xmlClientes = new File(rutaXml);
        if (!xmlClientes.exists()) {
            registro.registrar("lectura", "fallido", "eL archivo XML no existe");
            return listaClientes;
        } else {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // Mejora posterior a la entrega: se rechazan las declaraciones DOCTYPE
                // para evitar ataques XXE (lectura de ficheros externos desde un XML manipulado)
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(xmlClientes);
                document.getDocumentElement().normalize();

                NodeList listaNodos = document.getElementsByTagName("cliente");

                for (int i = 0; i < listaNodos.getLength(); i++) {

                    Element elemento = (Element) listaNodos.item(i);
                    String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
                    String direccion = elemento.getElementsByTagName("direccion").item(0).getTextContent();
                    String telefono = elemento.getElementsByTagName("telefono").item(0).getTextContent();
                    String email = elemento.getElementsByTagName("email").item(0).getTextContent();
                    Cliente cliente = new Cliente(nombre, direccion, telefono, email);
                    listaClientes.add(cliente);
                }
                registro.registrar("lectura", "exitoso", "");

            } catch (Exception e) {
                registro.registrar("lectura", "fallido", e.getMessage());
            }
        }
        return listaClientes;
    }

    public void guardarClientes(ArrayList<Cliente> clientes) {
        File archivo = new File(rutaXml);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element raiz = document.createElement("clientes");
            document.appendChild(raiz);
            for (Cliente c : clientes) {
                Element clienteElem = document.createElement("cliente");
                raiz.appendChild(clienteElem);
                Element nombreElem = document.createElement("nombre");
                nombreElem.setTextContent(c.getNombre());
                clienteElem.appendChild(nombreElem);
                Element direccionElem = document.createElement("direccion");
                direccionElem.setTextContent(c.getDireccion());
                clienteElem.appendChild(direccionElem);
                Element telefonoElem = document.createElement("telefono");
                telefonoElem.setTextContent(c.getTelefono());
                clienteElem.appendChild(telefonoElem);
                Element emailElem = document.createElement("email");
                emailElem.setTextContent(c.getEmail());
                clienteElem.appendChild(emailElem);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(archivo);

            transformer.transform(source, result);

            registro.registrar("escritura", "exitoso", "");

        } catch (Exception e) {
            registro.registrar("escritura", "fallido", e.getMessage());
        }
    }

    public void borrarXml() {
        File archivo = new File(rutaXml);
        if (!archivo.exists()) {
            registro.registrar("borrado", "fallido", "El fichero XML no existe");
            return;
        }
        if (archivo.delete()) {
            registro.registrar("borrado", "exitoso", "");
        } else {
            registro.registrar("borrado", "fallido", "No se pudo eliminar el fichero");
        }
    }
}
