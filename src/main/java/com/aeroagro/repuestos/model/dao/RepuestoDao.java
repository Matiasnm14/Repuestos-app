package com.aeroagro.repuestos.model.dao;

import com.aeroagro.repuestos.db.Dao;
import com.aeroagro.repuestos.db.DatabaseConnection;
import com.aeroagro.repuestos.model.entity.Repuesto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public class RepuestoDao implements Dao<Repuesto, String> {

    @Override
    public boolean save(Repuesto repuesto) throws SQLException {
        String sql = "INSERT INTO Repuesto (id, avion_id, tipo_repuesto_id, fecha, horas, num_parte, num_serie,ruta_img) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        Connection conn = DatabaseConnection.getConnection();

        if (repuesto.getId() == null || repuesto.getId().isEmpty()) {
            repuesto.setId(UUID.randomUUID().toString());
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, repuesto.getId());
            stmt.setString(2, repuesto.getAvionId());
            stmt.setString(3, repuesto.getTipoRepuestoId());
            stmt.setString(4, repuesto.getFecha());
            stmt.setInt(5, repuesto.getHoras());
            stmt.setString(6, repuesto.getNumParte());
            stmt.setString(7, repuesto.getNumSerie());
            stmt.setString(8,repuesto.getRuta());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Repuesto> findById(String id) throws SQLException {
        String sql = "SELECT id, avion_id, tipo_repuesto_id, fecha, horas, num_parte, num_serie, ruta_img FROM Repuesto WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Repuesto> findAll() throws SQLException {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT id, avion_id, tipo_repuesto_id, fecha, horas, num_parte, num_serie, ruta_img FROM Repuesto";
        Connection conn = DatabaseConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToEntity(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Repuesto repuesto) throws SQLException {
        String sql = "UPDATE Repuesto SET avion_id = ?, tipo_repuesto_id = ?, fecha = ?, horas = ?, num_parte = ?, num_serie = ?, ruta_img = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, repuesto.getAvionId());
            stmt.setString(2, repuesto.getTipoRepuestoId());
            stmt.setString(3, repuesto.getFecha());
            stmt.setInt(4, repuesto.getHoras());
            stmt.setString(5, repuesto.getNumParte());
            stmt.setString(6, repuesto.getNumSerie());
            stmt.setString(7, repuesto.getRuta());
            stmt.setString(8, repuesto.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Repuesto WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ==========================================
    // MÉTODOS DE FILTRADO ESPECÍFICOS
    // ==========================================

    /**
     * Obtiene todos los repuestos pertenecientes a un avión específico.
     */
    public List<Repuesto> findByAvionId(String avionId) throws SQLException {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT id, avion_id, tipo_repuesto_id, fecha, horas, num_parte, num_serie, ruta_img FROM Repuesto WHERE avion_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToEntity(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Filtra los repuestos por Avión y opcionalmente por Tipo de Repuesto.
     * Si tipoRepuestoId es nulo o vacío, devuelve todos los repuestos del avión.
     */
    public List<Repuesto> findByFiltros(String avionId, String tipoRepuestoId) throws SQLException {
        List<Repuesto> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT id, avion_id, tipo_repuesto_id, fecha, horas, num_parte, num_serie, ruta_img FROM Repuesto WHERE avion_id = ?"
        );

        boolean filtrarPorTipo = tipoRepuestoId != null && !tipoRepuestoId.trim().isEmpty();

        if (filtrarPorTipo) {
            sql.append(" AND tipo_repuesto_id = ?");
        }

        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setString(1, avionId);

            if (filtrarPorTipo) {
                stmt.setString(2, tipoRepuestoId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToEntity(rs));
                }
            }
        }
        return lista;
    }

    // Método auxiliar para evitar duplicación de mapeo
    private Repuesto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new Repuesto(
                rs.getString("id"),
                rs.getString("avion_id"),
                rs.getString("tipo_repuesto_id"),
                rs.getString("fecha"),
                rs.getInt("horas"),
                rs.getString("num_parte"),
                rs.getString("num_serie"),
                rs.getString("ruta_img")
        );
    }
}
