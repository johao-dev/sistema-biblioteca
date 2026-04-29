package com.universidadxyz.sistemabiblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDate fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDate fechaDevolucionReal; // Será null mientras no devuelva el libro

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Estado estado;

    /**
     * Método utilitario (Clean Code) para encapsular la lógica de negocio 
     * y saber si el préstamo está en mora directamente desde la entidad.
     */
    @Transient
    public boolean isEnMora() {
        if (estado.equals(Estado.DEVUELTO)) {
            return false;
        }
        return LocalDate.now().isAfter(fechaDevolucionEsperada);
    }

    public static enum Estado {
        ACTIVO, DEVUELTO, VENCIDO
    }
}