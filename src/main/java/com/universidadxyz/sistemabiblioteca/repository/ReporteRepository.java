package com.universidadxyz.sistemabiblioteca.repository;

import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@RequestScoped
public class ReporteRepository {

    @PersistenceContext(unitName = "BibliotecaPU")
    private EntityManager em;

    public List<Object[]> obtenerLibrosMasPrestados(int limite) {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT p.libro, COUNT(p) AS totalPrestamos " +
                "FROM Prestamo p " +
                "GROUP BY p.libro " +
                "ORDER BY totalPrestamos DESC",
                Object[].class);
        query.setHint("org.hibernate.cacheable", true);
        query.setMaxResults(limite);
        return query.getResultList();
    }

    public List<Prestamo> obtenerUsuariosMorosos() {
        TypedQuery<Prestamo> query = em.createQuery(
                "SELECT p FROM Prestamo p " +
                "JOIN FETCH p.usuario u " +
                "JOIN FETCH p.libro l " +
                "WHERE p.estado <> :estadoDevuelto " +
                "  AND p.fechaDevolucionEsperada < :hoy " +
                "ORDER BY p.fechaDevolucionEsperada ASC",
                Prestamo.class);
        query.setParameter("estadoDevuelto", Prestamo.Estado.DEVUELTO);
        query.setParameter("hoy", LocalDate.now());
        query.setHint("org.hibernate.cacheable", true);
        return query.getResultList();
    }
}
