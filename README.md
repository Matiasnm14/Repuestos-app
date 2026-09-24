# Repuestos App

Aplicación de escritorio para registrar aeronaves y los repuestos asociados a cada una. Permite organizar los componentes por tipo, consultar su información y mantener los datos localmente con SQLite. Está desarrollada en Java 21 con una interfaz Swing y no requiere un servidor para su uso.

## Funcionalidades

- Alta, edición y eliminación de aeronaves identificadas por matrícula o placa.
- Gestión de tipos de repuesto con nombre y descripción.
- Registro y edición de repuestos con fecha, horas de uso, número de parte, número de serie e imagen opcional.
- Consulta de repuestos por aeronave, filtro por tipo y búsqueda por número de parte o serie mientras se escribe.
- Copia de seguridad y restauración de la base de datos desde la interfaz.

Al eliminar una aeronave o un tipo de repuesto, SQLite elimina también sus repuestos asociados mediante `ON DELETE CASCADE`.

## Requisitos

Para compilar y ejecutar el proyecto desde el código fuente se necesitan:

- JDK 21.
- Apache Maven.
- Un entorno de escritorio compatible con Java Swing.

## Compilación y ejecución

Desde la raíz del repositorio:

```bash
mvn clean package
java -jar target/Repuestos-app-jar-with-dependencies.jar
```

Maven genera el JAR con dependencias durante la fase `package`. La base de datos y sus tablas se crean en el primer inicio de la aplicación.

### Instalador para Windows

Si la versión publicada incluye un instalador, puede descargarse desde [Releases](https://github.com/Matiasnm14/Repuestos-app/releases) y ejecutarse directamente. El instalador generado con `jpackage` incluye un entorno de ejecución de Java.

El flujo de GitHub Actions genera un instalador `RepuestosApp-<versión>-Windows.exe` al publicar una etiqueta `vMAJOR.MINOR.PATCH` o al iniciarlo manualmente. Usa el icono de `src/main/resources/icono.ico` para el instalador de Windows.

## Uso básico

1. Cree una aeronave con **Nuevo Avión** e introduzca su matrícula o placa.
2. Cree al menos un tipo con **Crear Tipo Repuesto**.
3. Seleccione la aeronave y use **Registrar Nuevo Repuesto en Avión** para añadir el componente. La fecha se propone en formato `AAAA-MM-DD`; las horas de uso deben ser un número entero no negativo.
4. Consulte los repuestos de la aeronave en la tabla. Use el selector **Tipo** o el campo **Buscar (N° Serie/Parte)** para filtrar los resultados.
5. Use el menú contextual sobre una aeronave, un tipo o un repuesto para editarlo o eliminarlo. En los repuestos también permite abrir la imagen asociada.

La tabla ordena los repuestos por el texto de la fecha, de más reciente a más antigua. Para conservar ese orden, introduzca las fechas en formato `AAAA-MM-DD`.

## Datos y copias de seguridad

La aplicación almacena los archivos en el directorio personal del usuario:

| Contenido | Ruta |
| --- | --- |
| Base de datos SQLite | `~/.repuestos_app/gestion_repuestos.db` |
| Imágenes de repuestos | `~/.repuestos_app/imagenes_repuestos/` |

Se admiten imágenes JPG, JPEG y PNG. Al seleccionarlas, la aplicación copia los archivos a la carpeta local y guarda su ruta en la base de datos. La conexión SQLite utiliza el modo WAL y habilita las claves foráneas.

**Crear Backup** solicita una carpeta de destino y crea una copia consolidada de la base de datos con esta estructura:

```text
Backup_AAAA-MM-DD/
└── gestion_repuestos_HH-MM-SS.db
```

**Cargar Backup** permite seleccionar un archivo `.db` y reemplaza la base de datos actual tras una confirmación. La restauración sobrescribe los registros existentes.

**Las copias de seguridad de la interfaz contienen solo la base de datos.** Las imágenes están en un directorio separado; para conservarlas, copie también `~/.repuestos_app/imagenes_repuestos/`. La base de datos guarda rutas absolutas de las imágenes, por lo que al restaurarla en otro equipo puede ser necesario volver a asociarlas.

## Organización del código

El proyecto separa la interfaz, la lógica de aplicación y el acceso a datos:

```text
src/main/java/com/aeroagro/repuestos/
├── Main.java                 # Inicio de la aplicación
├── view/                     # Ventana principal y diálogos Swing
├── controller/               # Operaciones y servicio de copias de seguridad
├── model/entity/             # Aeronaves, tipos y repuestos
├── model/dao/                # Consultas y cambios en SQLite
└── db/                       # Conexión e inicialización de tablas
```

Las entidades usan UUID como identificadores. La base de datos contiene las tablas `Avion`, `Tipo_Repuesto` y `Repuesto`; esta última referencia a las otras dos.

Las dependencias y la configuración de compilación están definidas en [`pom.xml`](pom.xml).
