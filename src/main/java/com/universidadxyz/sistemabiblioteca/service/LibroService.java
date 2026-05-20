package com.universidadxyz.sistemabiblioteca.service;

import com.universidadxyz.sistemabiblioteca.entity.Libro;
import com.universidadxyz.sistemabiblioteca.repository.LibroRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class LibroService {

    @Inject
    private LibroRepository libroRepository;

    public List<Map<String, Object>> listarTodos() {
        List<Libro> libros = libroRepository.findAllOrdenadoPorTitulo();
        return libros.stream().map(l -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", l.getId());
            dto.put("isbn", l.getIsbn());
            dto.put("titulo", l.getTitulo());
            dto.put("autor", l.getAutor());
            dto.put("cantidadEjemplares", l.getCantidadEjemplares());
            return dto;
        }).toList();
    }

    public Map<String, Object> consultarDisponibilidad(Long id) {
        Libro libro = libroRepository.findById(id);

        if (libro == null) {
            return null; // Indica que no se encontró
        }

        long prestamosActivos = libroRepository.countPrestamosActivos(id);
        long disponibles = libro.getCantidadEjemplares() - prestamosActivos;

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("libroId", libro.getId());
        respuesta.put("isbn", libro.getIsbn());
        respuesta.put("titulo", libro.getTitulo());
        respuesta.put("autor", libro.getAutor());
        respuesta.put("totalEjemplares", libro.getCantidadEjemplares());
        respuesta.put("prestamosActivos", prestamosActivos);
        respuesta.put("ejemplaresDisponibles", Math.max(disponibles, 0));
        respuesta.put("disponible", disponibles > 0);

        return respuesta;
    }

    public void guardarLibro(Libro libro) {
        libroRepository.save(libro);
    }

    public List<Libro> obtenerTodos() {
        return libroRepository.findAllOrdenadoPorTitulo();
    }
}
