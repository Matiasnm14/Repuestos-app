package com.aeroagro.repuestos.model.dao;



import com.aeroagro.repuestos.db.Dao;
import com.aeroagro.repuestos.db.DatabaseConnection;
import com.aeroagro.repuestos.model.entity.Avion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvionDao implements Dao<Avion, String> {

    @Override
    public boolean save(Avion avion) throws SQLException {
        String sql = "INSERT INTO Avion (id, placa) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();

        if (avion.getId() == null || avion.getId().isEmpty()) {
            avion.setId(java.util.UUID.randomUUID().toString());
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avion.getId());
            stmt.setString(2, avion.getPlaca());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Avion> findById(String id) throws SQLException {
        String sql = "SELECT id, placa FROM Avion WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Avion(rs.getString("id"), rs.getString("placa")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Avion> findAll() throws SQLException {
        List<Avion> aviones = new ArrayList<>();
        String sql = "SELECT id, placa FROM Avion";
        Connection conn = DatabaseConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                aviones.add(new Avion(rs.getString("id"), rs.getString("placa")));
            }
        }
        return aviones;
    }

    @Override
    public boolean update(Avion avion) throws SQLException {
        String sql = "UPDATE Avion SET placa = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avion.getPlaca());
            stmt.setString(2, avion.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Avion WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}