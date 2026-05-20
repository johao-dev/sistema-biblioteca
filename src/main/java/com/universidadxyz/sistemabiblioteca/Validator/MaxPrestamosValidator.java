package com.universidadxyz.sistemabiblioteca.validator;

import com.universidadxyz.sistemabiblioteca.repository.PrestamoRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

@FacesValidator(value = "maxPrestamosValidator", managed = true)
@RequestScoped
public class MaxPrestamosValidator implements Validator<Long> {

    @Inject
    private PrestamoRepository prestamoRepository;

    @Override
    public void validate(FacesContext context, UIComponent component, Long usuarioId) throws ValidatorException {
        if (usuarioId == null) {
            return;
        }

        long prestamosActivos = prestamoRepository.countPrestamosActivosPorUsuario(usuarioId);

        if (prestamosActivos >= 3) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Límite de préstamos excedido",
                    "El usuario ya tiene el máximo de 3 libros activos.");
            throw new ValidatorException(msg);
        }
    }
}