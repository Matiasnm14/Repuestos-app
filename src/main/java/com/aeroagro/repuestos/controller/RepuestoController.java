package com.aeroagro.repuestos.controller;

import com.aeroagro.repuestos.model.dao.RepuestoDao;
import com.aeroagro.repuestos.model.entity.Repuesto;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RepuestoController {

    private final RepuestoDao repuestoDAO;

    public RepuestoController() {
        this.repuestoDAO = new RepuestoDao();
    }

    /**
     * Registra un cambio de repuesto asociado a un avión.
     */
    public boolean registrarRepuesto(String avionId, String tipoRepuestoId, String fecha,
                                     int horas, String numParte, String numSerie,String ruta) throws IllegalArgumentException, RuntimeException {

        validarCampos(avionId, tipoRepuestoId, fecha, horas, numParte, numSerie);
        if(ruta == null)
            ruta = "";

        Repuesto repuesto = new Repuesto(
                avionId,
                tipoRepuestoId,
                fecha.trim(),
                horas,
                numParte.trim(),
                numSerie.trim(),
                ruta.trim()
        );

        try {
            return repuestoDAO.save(repuesto);
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar el repuesto: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene únicamente los repuestos instalados en un avión específico.
     */
    public List<Repuesto> obtenerRepuestosPorAvion(String avionId) throws RuntimeException {
        if (avionId == null || avionId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return repuestoDAO.findByAvionId(avionId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al filtrar repuestos por avión: " + e.getMessage(), e);
        }
    }

    public Optional<Repuesto> obtenerPorId(String id)  throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        try{
            return repuestoDAO.findById(id);
        }catch (SQLException e){
            throw new RuntimeException("Error al filtrar repuestos id: " + e.getMessage(), e);

        }
    }

    /**
     * Filtro principal: Permite seleccionar un avión y opcionalmente un tipo de repuesto.
     * Ideal para manejar los eventos de cambio en los filtros de la interfaz gráfica.
     */
    public List<Repuesto> filtrarRepuestos(String avionId, String tipoRepuestoId) throws RuntimeException {
        if (avionId == null || avionId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return repuestoDAO.findByFiltros(avionId, tipoRepuestoId);
        } catch (SQLException e) {
            throw new RuntimeException("Error al aplicar filtros a los repuestos: " + e.getMessage(), e);
        }
    }

    public boolean actualizarRepuesto(String id, String avionId, String tipoRepuestoId,
                                      String fecha, int horas, String numParte, String numSerie,String ruta) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del repuesto es obligatorio para actualizar.");
        }
        validarCampos(avionId, tipoRepuestoId, fecha, horas, numParte, numSerie);

        if(ruta == null)
            ruta = "";

        Repuesto repuesto = new Repuesto(
                id, avionId, tipoRepuestoId, fecha.trim(), horas, numParte.trim(), numSerie.trim(),ruta.trim()
        );

        try {
            return repuestoDAO.update(repuesto);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el repuesto: " + e.getMessage(), e);
        }
    }

    public boolean eliminarRepuesto(String id) throws RuntimeException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del repuesto no puede estar vacío.");
        }
        try {
            System.out.println("controller");
            return repuestoDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el repuesto: " + e.getMessage(), e);
        }
    }

    private void validarCampos(String avionId, String tipoRepuestoId, String fecha,
                               int horas, String numParte, String numSerie) {
        if (avionId == null || avionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un avión válido.");
        }
        if (tipoRepuestoId == null || tipoRepuestoId.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de repuesto válido.");
        }
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (horas < 0) {
            throw new IllegalArgumentException("Las horas de uso no pueden ser negativas.");
        }
        if (numParte == null || numParte.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de parte es obligatorio.");
        }
        if (numSerie == null || numSerie.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de serie es obligatorio.");
        }

    }
}
