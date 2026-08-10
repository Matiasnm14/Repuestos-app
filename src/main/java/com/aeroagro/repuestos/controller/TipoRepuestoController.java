package com.aeroagro.repuestos.controller;


import com.aeroagro.repuestos.model.dao.TipoRepuestoDao;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TipoRepuestoController {

    private final TipoRepuestoDao tipoRepuestoDAO;

    public TipoRepuestoController() {
        this.tipoRepuestoDAO = new TipoRepuestoDao();
    }

    public boolean registrarTipoRepuesto(String nombre, String descripcion) throws IllegalArgumentException, RuntimeException {
        validarNombre(nombre);

        TipoRepuesto tipo = new TipoRepuesto(
                nombre.trim(),
                descripcion != null ? descripcion.trim() : ""
        );

        try {
            return tipoRepuestoDAO.save(tipo);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el tipo de repuesto: " + e.getMessage(), e);
        }
    }

    public List<TipoRepuesto> listarTiposRepuesto() throws RuntimeException {
        try {
            return tipoRepuestoDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener tipos de repuesto: " + e.getMessage(), e);
        }
    }

    public Optional<TipoRepuesto> obtenerPorId(String id) throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return tipoRepuestoDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo de repuesto: " + e.getMessage(), e);
        }
    }

    public boolean actualizarTipoRepuesto(String id, String nombre, String descripcion) throws IllegalArgumentException, RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar el tipo de repuesto.");
        }
        validarNombre(nombre);

        TipoRepuesto tipo = new TipoRepuesto(
                id,
                nombre.trim(),
                descripcion != null ? descripcion.trim() : ""
        );

        try {
            return tipoRepuestoDAO.update(tipo);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar tipo de repuesto: " + e.getMessage(), e);
        }
    }

    public boolean eliminarTipoRepuesto(String id) throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacío.");
        }
        try {
            return tipoRepuestoDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el tipo de repuesto: " + e.getMessage(), e);
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo de repuesto es obligatorio.");
        }
    }
}
