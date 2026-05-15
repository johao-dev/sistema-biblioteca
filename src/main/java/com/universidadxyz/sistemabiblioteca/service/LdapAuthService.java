package com.universidadxyz.sistemabiblioteca.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de Autenticación LDAP (Axel).
 *
 * Responsabilidad:
 *   Validar que el usuario que intenta acceder al sistema es un estudiante o
 *   empleado activo de la Universidad Tecnológica XYZ, consultando el
 *   Directorio Activo (LDAP) centralizado de la institución.
 *
 * Cómo funciona LDAP (Lightweight Directory Access Protocol):
 *   - Es un protocolo estándar para gestionar usuarios en una organización.
 *   - La universidad tiene un servidor LDAP con todos los usuarios registrados.
 *   - Este servicio intenta "hacer un bind" (autenticarse) con las credenciales
 *     del usuario. Si el servidor LDAP acepta el bind, las credenciales son válidas.
 *   - Si el servidor rechaza el bind, lanza una AuthenticationException.
 *
 * @Named: Permite referenciar este bean desde las vistas JSF como #{ldapAuthService}.
 * @ApplicationScoped: Una sola instancia para toda la aplicación (es stateless,
 *                     no guarda estado de ningún usuario específico).
 */
@Named
@ApplicationScoped
public class LdapAuthService {

    private static final Logger LOG = Logger.getLogger(LdapAuthService.class.getName());

    // =========================================================================
    // CONFIGURACIÓN LDAP
    // En un sistema real, estos valores vendrían de un archivo de configuración
    // o variables de entorno. Aquí los dejamos como constantes documentadas.
    // =========================================================================

    /**
     * URL del servidor LDAP de la universidad.
     * Formato: ldap://[host]:[puerto]
     * Puerto estándar: 389 (sin SSL), 636 (con SSL/LDAPS)
     */
    private static final String LDAP_URL = "ldap://ldap.universidadxyz.edu:389";

    /**
     * Base DN (Distinguished Name): Define el "directorio raíz" donde se buscan usuarios.
     * Ejemplo: dc=universidadxyz,dc=edu
     *   - dc=universidadxyz → dominio principal
     *   - dc=edu             → sufijo institucional
     */
    private static final String BASE_DN = "dc=universidadxyz,dc=edu";

    /**
     * OU (Organizational Unit): Subdirectorio donde viven las cuentas de usuario.
     * Ejemplo: ou=usuarios → carpeta "usuarios" dentro del directorio.
     */
    private static final String USERS_OU = "ou=usuarios";

    // =========================================================================
    // MÉTODO PRINCIPAL DE AUTENTICACIÓN
    // =========================================================================

    /**
     * Verifica las credenciales de un usuario contra el servidor LDAP.
     *
     * Proceso:
     *   1. Construye el DN completo del usuario: uid=usuario,ou=usuarios,dc=universidadxyz,dc=edu
     *   2. Intenta crear una conexión LDAP autenticada con ese DN y la contraseña.
     *   3. Si la conexión es exitosa → credenciales válidas (retorna true).
     *   4. Si el servidor rechaza → AuthenticationException (retorna false).
     *   5. Si hay otro error (servidor caído, red, etc.) → lanza RuntimeException.
     *
     * @param username Nombre de usuario (ej: "axel.gomez"). Corresponde al campo
     *                 'usernameLdap' de la entidad Usuario.
     * @param password Contraseña del usuario en texto plano (LDAP la valida internamente).
     * @return true si las credenciales son válidas en el directorio de la universidad.
     * @throws RuntimeException Si hay un error de conexión con el servidor LDAP.
     */
    public boolean autenticar(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            LOG.warning("LDAP: Intento de autenticación con credenciales vacías.");
            return false;
        }

        // 1. Construir el DN completo del usuario
        // Formato: uid=axel.gomez,ou=usuarios,dc=universidadxyz,dc=edu
        String userDn = "uid=" + username + "," + USERS_OU + "," + BASE_DN;

        // 2. Configurar las propiedades de conexión LDAP
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple"); // Autenticación simple (usuario + contraseña)
        env.put(Context.SECURITY_PRINCIPAL, userDn);        // El "quién soy yo"
        env.put(Context.SECURITY_CREDENTIALS, password);    // La contraseña
        env.put("com.sun.jndi.ldap.connect.timeout", "5000"); // 5 segundos de timeout

        DirContext ctx = null;
        try {
            // 3. Intentar el bind (autenticación). Si falla → excepción.
            ctx = new InitialDirContext(env);
            LOG.info("LDAP: Autenticación exitosa para el usuario: " + username);
            return true; // ✅ Credenciales válidas

        } catch (AuthenticationException e) {
            // El servidor LDAP rechazó las credenciales (usuario o contraseña incorrectos)
            LOG.warning("LDAP: Credenciales inválidas para: " + username);
            return false; // ❌ Credenciales incorrectas

        } catch (NamingException e) {
            // Error de conexión: servidor caído, URL incorrecta, timeout, etc.
            LOG.log(Level.SEVERE, "LDAP: Error de conexión con el servidor LDAP: " + e.getMessage(), e);
            throw new RuntimeException("No se pudo conectar al servidor de autenticación. " +
                    "Por favor contacta al administrador.", e);

        } finally {
            // 4. Siempre cerrar la conexión LDAP para liberar recursos
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    LOG.warning("LDAP: Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Verifica si un usuario existe en el directorio LDAP (sin validar contraseña).
     * Útil para pre-validar el username antes de pedir la contraseña.
     *
     * Nota: Requiere un usuario de servicio con permisos de lectura en el LDAP.
     * Para el proyecto académico, este método es de referencia y documenta
     * el flujo completo de búsqueda LDAP.
     *
     * @param username El username a buscar.
     * @return true si el usuario existe en el directorio.
     */
    public boolean existeEnLdap(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        // Para buscar sin credenciales de usuario específico, necesitaríamos
        // un "service account" LDAP. En producción real esto se configura con
        // un usuario de solo lectura. Para el proyecto, delegamos la verificación
        // al método autenticar().
        LOG.info("LDAP: Verificación de existencia para usuario: " + username +
                 ". Redirigiendo a flujo de autenticación completo.");
        return true; // En un entorno real: buscar con cuenta de servicio.
    }
}
