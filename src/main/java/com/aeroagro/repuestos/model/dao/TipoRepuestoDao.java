package com.aeroagro.repuestos.model.dao;

import com.aeroagro.repuestos.db.Dao;
import com.aeroagro.repuestos.db.DatabaseConnection;
import com.aeroagro.repuestos.model.entity.Avion;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TipoRepuestoDao implements Dao<TipoRepuesto,String> {

    @Override
    public boolean save(TipoRepuesto entity) throws SQLException {
        String sql = "INSERT INTO Tipo_Repuesto (id,nombre,descripcion) VALUES (?,?,?)";
        Connection conn = DatabaseConnection.getConnection();
        if(entity.getId() == null || entity.getId().isEmpty()){
            entity.setId(UUID.randomUUID().toString());
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getId());
            stmt.setString(2, entity.getNombre());
            stmt.setString(3,entity.getDescripcion());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<TipoRepuesto> findById(String id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion FROM Tipo_Repuesto WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new TipoRepuesto(rs.getString("id"), rs.getString("nombre"),rs.getString("descripcion")));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<TipoRepuesto> findAll() throws SQLException {
        List<TipoRepuesto> repuestos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM Tipo_Repuesto";
        Connection conn = DatabaseConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                repuestos.add(new TipoRepuesto(rs.getString("id"), rs.getString("nombre"),rs.getString("descripcion")));
            }
        }
        return repuestos;
    }

    @Override
    public boolean update(TipoRepuesto entity) throws SQLException {
        String sql = "UPDATE Tipo_Repuesto SET nombre = ?, descripcion = ? WHERE id = ?";

        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getNombre());
            stmt.setString(2, entity.getDescripcion());
            stmt.setString(3, entity.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String s) throws SQLException {
        String sql = "DELETE FROM Tipo_Repuesto WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, s);
            return stmt.executeUpdate() > 0;
        }
    }
}
