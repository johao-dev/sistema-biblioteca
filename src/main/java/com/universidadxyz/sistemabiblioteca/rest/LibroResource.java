package com.universidadxyz.sistemabiblioteca.rest;

import com.universidadxyz.sistemabiblioteca.service.LibroService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * Recurso REST para consultar información de Libros (Axel).
 *
 * Ruta base: /api/libros
 * Endpoints disponibles:
 *   - GET /api/libros                        -> Lista todos los libros (título, autor, isbn)
 *   - GET /api/libros/{id}/disponibilidad    -> Retorna cuántas copias hay disponibles de un libro
 *
 * @Produces(APPLICATION_JSON): Todas las respuestas se serializan en formato JSON.
 */
@Path("/libros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibroResource {

    @Inject
    private LibroService libroService;

    // =========================================================================
    // GET /api/libros
    // Devuelve un listado de todos los libros registrados en el sistema.
    // =========================================================================
    @GET
    public Response listarTodos() {
        try {
            var resultado = libroService.listarTodos();
            return Response.ok(resultado).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error al obtener los libros: " + e.getMessage()))
                    .build();
        }
    }

    // =========================================================================
    // GET /api/libros/{id}/disponibilidad
    // Calcula en tiempo real cuántas copias de un libro están disponibles.
    // =========================================================================
    @GET
    @Path("/{id}/disponibilidad")
    public Response consultarDisponibilidad(@PathParam("id") Long id) {
        try {
            Map<String, Object> respuesta = libroService.consultarDisponibilidad(id);

            if (respuesta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "No se encontró el libro con ID: " + id))
                        .build();
            }

            return Response.ok(respuesta).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Error al consultar disponibilidad: " + e.getMessage()))
                    .build();
        }
    }
}
