package com.universidadxyz.sistemabiblioteca.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import com.universidadxyz.sistemabiblioteca.entity.Libro;
import jakarta.enterprise.context.RequestScoped;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestScoped
public class PDFReportesService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Colores del PDF
    private static final Color COLOR_CABECERA    = new Color(41, 128, 185);
    private static final Color COLOR_FILA_PAR    = new Color(235, 245, 255);
    private static final Color COLOR_MORA        = new Color(231, 76, 60);
    private static final Color COLOR_TEXTO_BLANCO = Color.WHITE;

    public byte[] generarPdfLibrosMasPrestados(List<Object[]> datos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc,
                    "Reporte: Libros Más Prestados",
                    "Universidad Tecnológica XYZ - Sistema de Biblioteca");

            PdfPTable tabla = new PdfPTable(new float[]{0.5f, 4f, 3f, 2f});
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(15f);

            agregarCeldasCabecera(tabla, "#", "Título del Libro", "Autor", "Total Préstamos");

            int posicion = 1;
            for (Object[] fila : datos) {
                var libro = (Libro) fila[0];
                Long total = (Long) fila[1];

                Color colorFila = (posicion % 2 == 0) ? COLOR_FILA_PAR : Color.WHITE;
                agregarCeldaDato(tabla, String.valueOf(posicion), colorFila);
                agregarCeldaDato(tabla, libro.getTitulo(), colorFila);
                agregarCeldaDato(tabla, libro.getAutor(), colorFila);
                agregarCeldaDato(tabla, String.valueOf(total), colorFila);
                posicion++;
            }

            doc.add(tabla);
            agregarPiePagina(doc);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de libros más prestados: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    public byte[] generarPdfUsuariosMorosos(List<Prestamo> morosos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc,
                    "Reporte: Usuarios con Préstamos Vencidos (Morosos)",
                    "Universidad Tecnológica XYZ - Sistema de Biblioteca");

            PdfPTable tabla = new PdfPTable(new float[]{2.5f, 2f, 3f, 2f, 2f, 1.5f});
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(15f);

            agregarCeldasCabecera(tabla,
                    "Nombre Usuario", "Username LDAP", "Libro Prestado",
                    "F. Préstamo", "F. Devolución Esperada", "Días Mora");

            for (Prestamo p : morosos) {
                long diasMora = LocalDate.now().toEpochDay() - p.getFechaDevolucionEsperada().toEpochDay();
                Color colorFila = (diasMora > 15)
                        ? new Color(255, 200, 200)
                        : new Color(255, 240, 200);

                agregarCeldaDato(tabla, p.getUsuario().getNombre(), colorFila);
                agregarCeldaDato(tabla, p.getUsuario().getUsernameLdap(), colorFila);
                agregarCeldaDato(tabla, p.getLibro().getTitulo(), colorFila);
                agregarCeldaDato(tabla, p.getFechaPrestamo().format(FORMATO_FECHA), colorFila);
                agregarCeldaDato(tabla, p.getFechaDevolucionEsperada().format(FORMATO_FECHA), colorFila);
                agregarCeldaDato(tabla, diasMora + " días", colorFila);
            }

            doc.add(tabla);

            Paragraph resumen = new Paragraph(
                    "\nTotal de préstamos vencidos: " + morosos.size(),
                    new Font(Font.HELVETICA, 10, Font.BOLD, COLOR_MORA));
            doc.add(resumen);

            agregarPiePagina(doc);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de morosos: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    private void agregarEncabezado(Document doc, String titulo, String subtitulo) throws DocumentException {
        Font fuenteTitulo    = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_CABECERA);
        Font fuenteSubtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
        Font fuenteFecha     = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);

        Paragraph pTitulo = new Paragraph(titulo, fuenteTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(pTitulo);

        Paragraph pSubtitulo = new Paragraph(subtitulo, fuenteSubtitulo);
        pSubtitulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(pSubtitulo);

        Paragraph pFecha = new Paragraph(
                "Generado el: " + LocalDate.now().format(FORMATO_FECHA), fuenteFecha);
        pFecha.setAlignment(Element.ALIGN_CENTER);
        pFecha.setSpacingAfter(10f);
        doc.add(pFecha);
    }

    private void agregarCeldasCabecera(PdfPTable tabla, String... encabezados) {
        Font fuenteCabecera = new Font(Font.HELVETICA, 10, Font.BOLD, COLOR_TEXTO_BLANCO);
        for (String texto : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteCabecera));
            celda.setBackgroundColor(COLOR_CABECERA);
            celda.setPadding(7f);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }
    }

    private void agregarCeldaDato(PdfPTable tabla, String texto, Color colorFondo) {
        Font fuente = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
        PdfPCell celda = new PdfPCell(new Phrase(texto != null ? texto : "-", fuente));
        celda.setBackgroundColor(colorFondo);
        celda.setPadding(5f);
        tabla.addCell(celda);
    }

    private void agregarPiePagina(Document doc) throws DocumentException {
        Font fuentePie = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
        Paragraph pie = new Paragraph(
                "\nSistema de Gestión de Biblioteca - Universidad Tecnológica XYZ | " +
                "Generado automáticamente por el Sistema", fuentePie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(20f);
        doc.add(pie);
    }
}
