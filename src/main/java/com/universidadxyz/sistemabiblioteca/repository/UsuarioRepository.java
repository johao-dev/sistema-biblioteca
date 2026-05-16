package com.universidadxyz.sistemabiblioteca.repository;

import com.universidadxyz.sistemabiblioteca.entity.Usuario;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RequestScoped
public class UsuarioRepository {

    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    public Usuario findById(Long id) {
        return em.find(Usuario.class, id);
    }
}