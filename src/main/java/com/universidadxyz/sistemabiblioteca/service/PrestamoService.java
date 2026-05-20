package com.universidadxyz.sistemabiblioteca.service;

import com.universidadxyz.sistemabiblioteca.entity.Libro;
import com.universidadxyz.sistemabiblioteca.entity.Prestamo;
import com.universidadxyz.sistemabiblioteca.entity.Usuario;
import com.universidadxyz.sistemabiblioteca.repository.LibroRepository;
import com.universidadxyz.sistemabiblioteca.repository.PrestamoRepository;
import com.universidadxyz.sistemabiblioteca.repository.UsuarioRepository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@RequestScoped
public class PrestamoService {

    @Inject
    private PrestamoRepository prestamoRepository;

    @Inject
    private LibroRepository libroRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Prestamo registrarPrestamo(Long usuarioId, Long libroId) throws Exception {
        Usuario usuario = usuarioRepository.findById(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }
        if (!usuario.getActivo()) {
            throw new Exception("El usuario no está activo.");
        }

        long prestamosActivos = prestamoRepository.countPrestamosActivosPorUsuario(usuarioId);
        if (prestamosActivos >= 3) {
            throw new Exception("El usuario ya tiene el máximo de 3 libros prestados.");
        }

        Libro libro = libroRepository.findById(libroId);
        if (libro == null) {
            throw new Exception("Libro no encontrado.");
        }

        long prestamosLibro = libroRepository.countPrestamosActivos(libroId);
        if (prestamosLibro >= libro.getCantidadEjemplares()) {
            throw new Exception("No hay ejemplares disponibles de este libro.");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(14));
        prestamo.setEstado(Prestamo.Estado.ACTIVO);

        prestamoRepository.save(prestamo);
        return prestamo;
    }

    @Transactional
    public Prestamo registrarDevolucion(Long prestamoId) throws Exception {
        Prestamo prestamo = prestamoRepository.findById(prestamoId);
        if (prestamo == null) {
            throw new Exception("Préstamo no encontrado.");
        }

        if (prestamo.getEstado() == Prestamo.Estado.DEVUELTO) {
            throw new Exception("El libro ya fue devuelto.");
        }

        prestamo.setEstado(Prestamo.Estado.DEVUELTO);
        prestamo.setFechaDevolucionReal(LocalDate.now());

        prestamoRepository.save(prestamo);
        return prestamo;
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(Long usuarioId) {
        return prestamoRepository.findPrestamosByUsuario(usuarioId);
    }

    public List<Prestamo> obtenerTodos() {
        return prestamoRepository.findAll();
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Libro> obtenerLibros() {
        return libroRepository.findAllOrdenadoPorTitulo();
    }
}