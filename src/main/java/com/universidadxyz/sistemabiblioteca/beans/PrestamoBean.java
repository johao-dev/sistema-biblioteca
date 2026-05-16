package com.universidadxyz.sistemabiblioteca.beans;

import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import com.universidadxyz.sistemabiblioteca.service.PrestamoService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
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

    public void registrarPrestamo() {
        try {
            prestamoService.registrarPrestamo(usuarioId, libroId);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Préstamo registrado correctamente."));
            libroId = null;
            if (usuarioId != null) {
                cargarPrestamosUsuario();
            }
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
            if (usuarioId != null) {
                cargarPrestamosUsuario();
            }
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
