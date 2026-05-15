package com.universidadxyz.sistemabiblioteca.entity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Entidad Libro.
 * @Cacheable: Indica a JPA que esta entidad puede ser almacenada en el caché de segundo nivel.
 * @Cache: Configuración específica de Hibernate. READ_WRITE es ideal para datos que se
 *         leen frecuentemente pero que también pueden actualizarse (agregar/quitar libros).
 */

@Entity
@Table(name = "libros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(name = "cantidad_ejemplares", nullable = false)
    private int cantidadEjemplares;

    @ToString.Exclude
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prestamo> prestamos;
}