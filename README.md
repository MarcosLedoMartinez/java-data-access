# Java Data Access

Proyectos de persistencia de datos en Java realizados durante el CFGS de **Desarrollo de Aplicaciones Multiplataforma (DAM)** en LinkiaFP.

Cada carpeta es un módulo Maven que resuelve el mismo tipo de problema (guardar y recuperar información) con una tecnología distinta: ficheros XML, bases de datos relacionales con JDBC y bases de datos XML nativas.

## Módulos

| Módulo | Tecnologías | Qué incluye |
|---|---|---|
| [`01-xml-dom-clientes`](01-xml-dom-clientes) | Java DOM (`javax.xml`), regex | Gestor de clientes por consola (alta, modificación, baja y listado) con persistencia en XML. Valida nombre, teléfono y email con expresiones regulares, impide emails duplicados y registra cada operación en un fichero de log con fecha y resultado. |
| [`02-jdbc-mysql-tienda`](02-jdbc-mysql-tienda) | JDBC, MySQL | Gestión de ventas y stock con **transacciones** (`commit` / `rollback`): una venta solo se registra si hay stock suficiente y ambas operaciones se confirman juntas. Crea la base de datos y las tablas (con clave foránea) si no existen, usa `PreparedStatement` y traduce los códigos de error de MySQL (1044, 1045, 1049, 2003) a mensajes claros. |
| [`03-jdbc-mysql-matriculas`](03-jdbc-mysql-matriculas) | JDBC, MySQL | Matrícula de alumnos en cursos (relación 1:N) con alta, modificación, baja y listados mediante `JOIN`. Valida el formato del email, la fecha de nacimiento y una edad mínima de 18 años. |
| [`04-xml-nativa-existdb`](04-xml-nativa-existdb) | eXist-db, XML:DB API, XQuery | Conexión a eXist-db, creación de colecciones e inserción, consulta, modificación (XQuery Update) y borrado de documentos XML. |
| [`05-xml-nativa-basex`](05-xml-nativa-basex) | BaseX, XQuery | Sesión cliente con BaseX: creación de una base de datos a partir de XML, consultas XPath/XQuery e inserción de nodos con XQuery Update, mostrando el estado antes y después. |

## Requisitos

- JDK 21 o superior (desarrollado con JDK 25)
- Maven 3.9 o superior
- Según el módulo: MySQL 8+, eXist-db 6 o BaseX 12 en local

## Cómo ejecutarlo

```bash
# Compilar todos los módulos
mvn compile

# Ejecutar un módulo concreto (desde su carpeta)
cd 01-xml-dom-clientes
mvn compile exec:java
```

El módulo 01 no necesita base de datos: trabaja sobre `data/clientes.xml`, que incluye datos de ejemplo ficticios.

Hay que ejecutarlo con la carpeta del módulo como directorio de trabajo. Con Maven ya ocurre al lanzarlo desde `01-xml-dom-clientes`; en IntelliJ hay que indicar esa carpeta en *Working directory* dentro de la configuración de ejecución.

Las aplicaciones JDBC (02 y 03) crean su base de datos (`tiendaDB` y `FPLinkia`) y sus tablas la primera vez que se ejecutan, así que el usuario de MySQL necesita permiso para crear bases de datos.

## Configuración

Las credenciales no están en el código: se leen de variables de entorno. Si no existen, se usan los valores por defecto de una instalación local.

| Variable | Valor por defecto | Módulos |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/` | 02, 03 |
| `DB_USER` | `root` | 02, 03 |
| `DB_PASSWORD` | *(vacío)* | 02, 03 |
| `EXIST_URI` | `xmldb:exist://localhost:8080/exist/xmlrpc` | 04 |
| `EXIST_USER` / `EXIST_PASSWORD` | `admin` / *(vacío)* | 04 |
| `BASEX_HOST` / `BASEX_PORT` | `localhost` / `1984` | 05 |
| `BASEX_USER` / `BASEX_PASSWORD` | `admin` / `admin` | 05 |

Ejemplo en PowerShell (Windows):

```powershell
$env:DB_PASSWORD = "mi_clave"
mvn compile exec:java
```

Ejemplo en Linux o macOS:

```bash
DB_PASSWORD=mi_clave mvn compile exec:java
```

## Estructura

```
java-data-access/
├── pom.xml                       # POM padre (agrupa los 5 módulos)
├── 01-xml-dom-clientes/
├── 02-jdbc-mysql-tienda/
├── 03-jdbc-mysql-matriculas/
├── 04-xml-nativa-existdb/
└── 05-xml-nativa-basex/
```

Todos los módulos usan el paquete base `com.marcosledo.accesodatos`.

## Qué crea o modifica cada demo

Conviene ejecutarlas contra servidores de prueba:

- **01:** reescribe `data/clientes.xml` al guardar y lo borra con la opción 7. También crea `data/log.txt`.
- **02:** crea la base de datos `tiendaDB` con tres productos de ejemplo. Cada venta descuenta stock.
- **03:** crea la base de datos `FPLinkia` con seis cursos. Las opciones de alta, modificación y baja trabajan sobre la tabla `alumno`.
- **04:** crea la colección `/db/Biblioteca` y, dentro de ella, guarda, modifica y borra `libros.xml`.
- **05:** crea la base de datos `AutoresDB`. Si ya existe una con ese nombre, BaseX la sobrescribe.

## Limitaciones conocidas

Son trabajos académicos y se publican tal como se entregaron, con estos defectos conocidos:

- **01:** si el XML está dañado, la opción "Cargar lista desde XML" deja la lista en memoria vacía.
- **02 y 03:** la conexión inicial al servidor se sustituye por la de la base de datos sin cerrarse antes.
- **02:** el id de la venta se calcula con `MAX(id_venta) + 1` y el stock se comprueba sin bloquear la fila. Con varias ventas simultáneas podría generar ids repetidos o vender más unidades de las disponibles.

## Cambios respecto a la entrega original

- Las credenciales salen del código y se leen de variables de entorno.
- Se protege el parser XML del módulo 01 frente a ataques XXE: rechaza documentos con `DOCTYPE`.
- Los proyectos se reorganizan como módulos Maven con paquete `com.marcosledo.accesodatos`.
- Los datos de ejemplo pasan a ser ficticios, con emails en `example.com`.
- No se incluyen la actividad 1 de la UT2, que repite el planteamiento del módulo de matrículas, ni el proyecto de Hibernate, que no llegó a desarrollarse.

Los ejercicios son trabajo académico propio. La adaptación para GitHub se preparó con soporte de herramientas de IA; los cambios respecto al original y las limitaciones conocidas se indican en este README.

## Contexto

Trabajos de los módulos *Acceso a Datos* y *Programación* de 1º de DAM (LinkiaFP, curso 2025-26). El código y los comentarios están en español.

**Autor:** Marcos Ledo Martínez · [LinkedIn](https://www.linkedin.com/in/marcos-ledo-martinez-697b93bb)
