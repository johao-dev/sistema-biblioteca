package com.universidadxyz.sistemabiblioteca.repository;

import com.universidadxyz.sistemabiblioteca.entity.Libro;
import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class LibroRepository {

    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    public List<Libro> findAllOrdenadoPorTitulo() {
        TypedQuery<Libro> query = em.createQuery(
                "SELECT l FROM Libro l ORDER BY l.titulo ASC", Libro.class);
        query.setHint("jakarta.persistence.cache.storeMode", "USE");
        return query.getResultList();
    }

    public Libro findById(Long id) {
        return em.find(Libro.class, id);
    }

    public long countPrestamosActivos(Long libroId) {
        TypedQuery<Long> queryActivos = em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p " +
                "WHERE p.libro.id = :libroId AND p.estado = :estado",
                Long.class);
        queryActivos.setParameter("libroId", libroId);
        queryActivos.setParameter("estado", Prestamo.Estado.ACTIVO);
        return queryActivos.getSingleResult();
    }

    @jakarta.transaction.Transactional
    public void save(Libro libro) {
        if (libro.getId() == null) {
            em.persist(libro);
        } else {
            em.merge(libro);
        }
    }
}
