package com.universidadxyz.sistemabiblioteca.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Clase de activación de JAX-RS (Axel).
 *
 * La anotación @ApplicationPath define la raíz de todos los endpoints REST.
 * Cualquier recurso REST que creemos estará disponible bajo la ruta:
 *   http://localhost:8080/sistema-biblioteca/api/...
 *
 * Al extender Application sin sobreescribir nada, JAX-RS escaneará
 * automáticamente el classpath en busca de clases anotadas con @Path.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // No se necesita código aquí.
    // GlassFish detecta esta clase y activa el subsistema JAX-RS automáticamente.
}
