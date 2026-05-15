package com.universidadxyz.sistemabiblioteca.service;

import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import com.universidadxyz.sistemabiblioteca.repository.ReporteRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 * Servicio de Generación de Reportes (Axel).
 *
 * Responsabilidades:
 *   Orquestar la consulta de datos y la generación de reportes en PDF,
 *   delegando el acceso a datos al Repository y la generación del PDF al servicio especializado.
 *
 * @Named: Permite que este bean sea referenciado desde las vistas JSF.
 * @RequestScoped: Se crea una instancia nueva por cada petición HTTP.
 */
@Named
@RequestScoped
public class ReporteService {

    @Inject
    private ReporteRepository reporteRepository;

    @Inject
    private PDFReportesService pdfReportesService;

    // =========================================================================
    // JPQL 1: Libros más prestados
    // =========================================================================

    public List<Object[]> obtenerLibrosMasPrestados(int limite) {
        return reporteRepository.obtenerLibrosMasPrestados(limite);
    }

    public byte[] generarPdfLibrosMasPrestados(int limite) {
        List<Object[]> datos = obtenerLibrosMasPrestados(limite);
        return pdfReportesService.generarPdfLibrosMasPrestados(datos);
    }

    // =========================================================================
    // JPQL 2: Usuarios Morosos
    // =========================================================================

    public List<Prestamo> obtenerUsuariosMorosos() {
        return reporteRepository.obtenerUsuariosMorosos();
    }

    public byte[] generarPdfUsuariosMorosos() {
        List<Prestamo> morosos = obtenerUsuariosMorosos();
        return pdfReportesService.generarPdfUsuariosMorosos(morosos);
    }
}
