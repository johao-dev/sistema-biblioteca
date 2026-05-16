package com.universidadxyz.sistemabiblioteca.repository;

import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;

@RequestScoped
public class PrestamoRepository {

    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    @Transactional
    public void save(Prestamo prestamo) {
        if (prestamo.getId() == null) {
            em.persist(prestamo);
        } else {
            em.merge(prestamo);
        }
    }

    public Prestamo findById(Long id) {
        return em.find(Prestamo.class, id);
    }

    public long countPrestamosActivosPorUsuario(Long usuarioId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.estado = :estado",
                Long.class);
        query.setParameter("usuarioId", usuarioId);
        query.setParameter("estado", Prestamo.Estado.ACTIVO);
        return query.getSingleResult();
    }
    
    public List<Prestamo> findPrestamosByUsuario(Long usuarioId) {
        TypedQuery<Prestamo> query = em.createQuery(
                "SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId ORDER BY p.fechaPrestamo DESC",
                Prestamo.class);
        query.setParameter("usuarioId", usuarioId);
        return query.getResultList();
    }
}
