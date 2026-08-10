package com.aeroagro.repuestos.controller;

import com.aeroagro.repuestos.model.dao.AvionDao;
import com.aeroagro.repuestos.model.entity.Avion;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AvionController {

    private final AvionDao avionDAO;

    public AvionController() {
        this.avionDAO = new AvionDao();
    }

    /**
     * Registra un nuevo avión en el sistema validando sus datos.
     */
    public boolean registrarAvion(String placa) throws IllegalArgumentException, RuntimeException {
        validarPlaca(placa);

        Avion nuevoAvion = new Avion(placa.trim().toUpperCase());

        try {
            return avionDAO.save(nuevoAvion);
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el avión en la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el listado completo de aviones para poblar tablas o ComboBoxes.
     */
    public List<Avion> listarAviones() throws RuntimeException {
        try {
            return avionDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar la lista de aviones: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un avión por su ID único.
     */
    public Optional<Avion> obtenerPorId(String id) throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return avionDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el avión por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza la matrícula/placa de un avión existente.
     */
    public boolean actualizarAvion(String id, String nuevaPlaca) throws IllegalArgumentException, RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del avión es obligatorio para actualizar.");
        }
        validarPlaca(nuevaPlaca);

        Avion avion = new Avion(id, nuevaPlaca.trim().toUpperCase());

        try {
            return avionDAO.update(avion);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la información del avión: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un avión por su ID.
     */
    public boolean eliminarAvion(String id) throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del avión no puede estar vacío.");
        }
        try {
            return avionDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el avión: " + e.getMessage(), e);
        }
    }

    private void validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula/placa del avión es obligatoria.");
        }
    }
}
