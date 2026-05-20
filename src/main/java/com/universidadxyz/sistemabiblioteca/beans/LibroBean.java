package com.universidadxyz.sistemabiblioteca.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;

import java.util.List;

import com.universidadxyz.sistemabiblioteca.entity.Libro;
import com.universidadxyz.sistemabiblioteca.service.LibroService;

@Named
@RequestScoped
public class LibroBean {

    private Libro libro = new Libro();

    @Inject
    private LibroService libroService;

    private List<Libro> librosRegistrados;

    @PostConstruct
    public void init() {
        librosRegistrados = libroService.obtenerTodos();
    }

    public List<Libro> getLibrosRegistrados() {
        return librosRegistrados;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public String guardar() {
        try {
            libroService.guardarLibro(libro);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Libro registrado correctamente."));
            libro = new Libro(); // Limpiar formulario
            librosRegistrados = libroService.obtenerTodos(); // Refrescar tabla
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el libro: " + e.getMessage()));
        }

        return null;
    }
}