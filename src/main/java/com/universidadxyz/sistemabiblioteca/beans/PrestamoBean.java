package com.universidadxyz.sistemabiblioteca.beans;

import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import com.universidadxyz.sistemabiblioteca.service.PrestamoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

import com.universidadxyz.sistemabiblioteca.entity.Usuario;
import com.universidadxyz.sistemabiblioteca.entity.Libro;

@Named
@RequestScoped
public class PrestamoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PrestamoService prestamoService;

    @Getter
    @Setter
    private Long usuarioId;

    @Getter
    @Setter
    private Long libroId;

    @Getter
    @Setter
    private Long prestamoId;

    @Getter
    private List<Prestamo> prestamosUsuario;

    @Getter
    private List<Prestamo> todosPrestamos;

    @Getter
    private List<Usuario> usuariosDisponibles;

    @Getter
    private List<Libro> librosDisponibles;

    @PostConstruct
    public void init() {
        cargarListas();
    }

    public void cargarListas() {
        todosPrestamos = prestamoService.obtenerTodos();
        usuariosDisponibles = prestamoService.obtenerUsuarios();
        librosDisponibles = prestamoService.obtenerLibros();
    }

    public void registrarPrestamo() {
        try {
            prestamoService.registrarPrestamo(usuarioId, libroId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Préstamo registrado correctamente."));
            libroId = null;
            usuarioId = null;
            cargarListas();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void registrarDevolucion() {
        try {
            prestamoService.registrarDevolucion(prestamoId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Devolución registrada correctamente."));
            prestamoId = null;
            cargarListas();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void cargarPrestamosUsuario() {
        if (usuarioId != null) {
            prestamosUsuario = prestamoService.obtenerPrestamosPorUsuario(usuarioId);
        } else {
            prestamosUsuario = null;
        }
    }
}