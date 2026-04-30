# Sistema de Gestión de Biblioteca - Universidad Tecnológica XYZ

**Proyecto Final - Curso: Gestores de Administración Web**

## Descripción del Proyecto

La biblioteca de la Universidad Tecnológica XYZ gestiona más de 10,000 libros físicos, 5,000 usuarios y 500 préstamos mensuales.
El sistema anterior, basado en procesos manuales y hojas de cálculo, generaba un 30% de retrasos, 15% de pérdida de registros y
sobrecarga laboral, sin integración con el sistema central de la universidad.

Este proyecto es un sistema web robusto diseñado para modernizar esta gestión. Permite el registro eficiente de libros,
administración automatizada de préstamos/devoluciones, generación de reportes precisos (margen de error < 2%) e integración con
LDAP para validación de estudiantes activos.

## Tecnologías Obligatorias

* **Frontend / UI:** JSF (JavaServer Faces) con Facelets.
* **Persistencia:** JPA / Hibernate (Caché de segundo nivel configurado).
* **Inyección de Dependencias:** CDI.
* **Servicios:** JAR-RS (RESTful).
* **Base de Datos:** MySQL 8.
* **Servidor de Aplicaciones:** Eclipse GlassFish 7.
* **IDE Recomendado:** Visual Studio Code.

## Configuración del Entorno Local

Para ejecutar este proyecto en tu máquina, sigue estos pasos:

1. **Clonar el repositorio:**
    ```bash
    git clone https://github.com/johao-dev/sistema-biblioteca.git
    ```
2. **Preparar la Base de Datos:**

Abre MySQL Workbench y ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS biblioteca_xyz;
```

*Nota: Las tablas (`usuarios`, `libros`, `prestamos`) se generarán automáticamente gracias a la configuración de Hibernate al
desplegar la aplicación*

3. **Compilar el proyecto:**

Abre el proyecto en VS Code y ejecuta en la terminal:

```bash
mvn clean package
```

4. **Desplegar:**

Sube el archivo `sistema-biblioteca.war` (generado en la carpeta `target/`) a tu servidor GlassFish local a través de la consola
de administración (`http://localhost:4848`).

## Estructura del proyecto

El proyecto sigue una arquitectura en capas limpia para separar la interfaz, la lógica de negocio y el acceso a datos. Todos los
miembros deben respetar esta estructura al crear nuevos archivos:

```text
📦 sistema-biblioteca
┣ 📂 src/main/java/com/universidadxyz/sistemabiblioteca
┃ ┣ 📂 beans        # Controladores JSF (@Named, @SessionScoped)
┃ ┣ 📂 config       # Configuración inicial (Ej. JpaConfig)
┃ ┣ 📂 entity       # Entidades JPA (@Entity: Libro, Usuario, Prestamo)
┃ ┣ 📂 rest         # Endpoints REST (JAX-RS)
┃ ┗ 📂 services     # Lógica de negocio, validaciones
┃ ┗ 📂 repository   # Repositorios JPA y consultas JPQL
┣ 📂 src/main/resources/META-INF
┃ ┗ 📜 persistence.xml  # Configuración de conexión a la BD MySQL
┣ 📂 src/main/webapp
┃ ┣ 📂 WEB-INF
┃ ┃ ┣ 📜 beans.xml      # Activador de Inyección de Dependencias (CDI)
┃ ┃ ┗ 📜 web.xml        # Configuración principal de la aplicación web
┃ ┣ 📂 view             # (Opcional) Subcarpeta para organizar las pantallas JSF
┃ ┗ 📜 index.xhtml      # Dashboard o menú principal de la UI
┗ 📜 pom.xml            # Gestor de dependencias Maven (Lombok, Jakarta EE, MySQL, etc.)
```

## División del Trabajo y Responsabilidades

A continuación se detalla la asignación de tareas para el cumplimiento de los requerimientos del caso práctico:

**JOHAO: Backend, Base de Datos y Git**

* Crear el repositorio en GitHub y agregar colaboradores.
* Subir la estructura inicial y limpia del proyecto.
* Crear las entidades JPA (`Libro`, `Usuario`, `Prestamo`).
* Definir relaciones (`OneToMany`, `@ManyToOne`)
* Configurar la conexión local con MySQL (`persistence.xml` y by-pass de GlassFish).
* Integrar (unir) todo el sistema y resolver conflictos de ramas.

**SEBASTIAN: Lógica del Sistema**

* Implementar Managed Beans (`@Named`, `@SessionScoped`) para gestionar el ciclo de vida.
* Programar lógica de registro de préstamos.
* Programar lógica de registro de devoluciones.
* Implementar validador personalizado (Máximo 3 libros activos por usuario).
* Apoyar en la integración final del sistema.

**GINA y ROXANA: Interfaz Gráfica (JSF)**

* Diseñar las pantallas principales del sistema (`.xhtml`).
* Crear formularios accesibles para:
    * Registro de libros
    * Préstamos
    * Devoluciones
* Agregar botones de acción (guardar, prestar, devolver) y conectarlos con la lógica.
* Integrar convertidores para fechas (ej. `<f:convertDateTime>`).
* Implementar validaciones visuales en los formularios.

**AXEL: Reportes y Servicios REST**

* Configurar Hibernate con caché de segundo nivel para optimizar rendimiento.
* Crear consultar JPQL para reportes.
* Genera automatizados (PDF o Excel) de:
    * Libros más prestados
    * Usuarios morosos
* Desarrollar servicios RESTful (JAX-RS) para consultar disponibilidad de libros.
* Establecer la integración con LDAP para autenticación centralizada.

---

*Para cualquier duda con la arquitectura o configuración base, consultar con Johao antes de modificar los
archivos `.xml` o configuraciones principales.*