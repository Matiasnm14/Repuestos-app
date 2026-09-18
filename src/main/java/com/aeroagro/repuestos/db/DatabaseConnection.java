package com.aeroagro.repuestos.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".repuestos_app";
    private static final String DB_PATH = DB_DIR + File.separator + "gestion_repuestos.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static Connection connection;

    static {
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL;");
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public static void initDatabase() {
        String sqlAvion = """
        CREATE TABLE IF NOT EXISTS Avion (
            id TEXT PRIMARY KEY,
            placa TEXT NOT NULL UNIQUE
        );
    """;

        String sqlTipoRepuesto = """
        CREATE TABLE IF NOT EXISTS Tipo_Repuesto (
            id TEXT PRIMARY KEY,
            nombre TEXT NOT NULL,
            descripcion TEXT
        );
    """;

        String sqlRepuesto = """
        CREATE TABLE IF NOT EXISTS Repuesto (
            id TEXT PRIMARY KEY,
            avion_id TEXT NOT NULL,
            tipo_repuesto_id TEXT NOT NULL,
            fecha TEXT NOT NULL,
            horas INTEGER NOT NULL DEFAULT 0,
            num_parte TEXT NOT NULL,
            num_serie TEXT NOT NULL,
            ruta_img TEXT,
            FOREIGN KEY (avion_id) REFERENCES Avion(id) ON DELETE CASCADE,
            FOREIGN KEY (tipo_repuesto_id) REFERENCES Tipo_Repuesto(id) ON DELETE CASCADE
        );
    """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlAvion);
            stmt.execute(sqlTipoRepuesto);
            stmt.execute(sqlRepuesto);

            System.out.println("Tablas 'Avion', 'Tipo_Repuesto' y 'Repuesto' creadas o verificadas con éxito.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar las tablas en la base de datos", e);
        }
    }

    public static void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
