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

    public java.util.List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombre ASC", Usuario.class).getResultList();
    }
}