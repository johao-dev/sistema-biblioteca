package com.universidadxyz.sistemabiblioteca.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JpaConfig {

    private EntityManagerFactory emf;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        System.out.println("==================================================");
        System.out.println("Iniciando Hibernate manualmente (Bypass Glassfish/Derby)...");
        
        emf = Persistence.createEntityManagerFactory("BibliotecaPU");
        
        System.out.println("¡Tablas de MySQL verificadas y listas!");
        System.out.println("==================================================");
    }

    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CORREGIDO: Solo usamos @Disposes. CDI ya sabe que debe ejecutar
     * este método automáticamente al terminar el RequestScoped.
     */
    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

    @PreDestroy
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}