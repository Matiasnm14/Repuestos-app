package com.aeroagro.repuestos.controller;


import com.aeroagro.repuestos.db.DatabaseConnection;
import com.aeroagro.repuestos.view.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupService {

    public static void realizarCopiaSeguridad(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione la carpeta de destino para el respaldo");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File carpetaDestinoRaiz = chooser.getSelectedFile();
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String horaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));

            File carpetaFecha = new File(carpetaDestinoRaiz, "Backup_" + fechaActual);
            if (!carpetaFecha.exists()) {
                carpetaFecha.mkdirs();
            }

            File dbDestino = new File(carpetaFecha, "gestion_repuestos_" + horaActual + ".db");
            String rutaDestino = dbDestino.getAbsolutePath().replace("\\", "/");

            // VACUUM INTO consolida el .db principal con los cambios del .db-wal en un solo archivo limpio
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.execute("VACUUM INTO '" + rutaDestino + "'");

                JOptionPane.showMessageDialog(parent,
                        "¡Copia de seguridad realizada con éxito!\n\nGuardado en:\n" + dbDestino.getAbsolutePath(),
                        "Backup Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "Error al realizar la copia de seguridad: " + ex.getMessage(),
                        "Error de Backup",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void restaurarCopiaSeguridad(MainFrame parent, Runnable onRestoreSuccess) {
        int confirmacion = JOptionPane.showConfirmDialog(
                parent,
                "⚠️ ¡ATENCIÓN!\nRestaurar una copia sobrescribirá los datos actuales.\n¿Desea continuar?",
                "Confirmar Restauración",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione el archivo de respaldo (.db)");
        chooser.setFileFilter(new FileNameExtensionFilter("Base de Datos SQLite (*.db)", "db"));

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File archivoBackup = chooser.getSelectedFile();

            String userHome = System.getProperty("user.home");
            File dirApp = new File(userHome + File.separator + ".repuestos_app");
            File dbActual = new File(dirApp, "gestion_repuestos.db");
            File walActual = new File(dirApp, "gestion_repuestos.db-wal");
            File shmActual = new File(dirApp, "gestion_repuestos.db-shm");

            try {
                // 1. Cerrar conexión para liberar archivos
                DatabaseConnection.cerrarConexion();

                // 2. Eliminar archivos temporales WAL y SHM si existen para prevenir conflictos
                if (walActual.exists()) walActual.delete();
                if (shmActual.exists()) shmActual.delete();

                // 3. Reemplazar la base de datos principal
                Files.copy(archivoBackup.toPath(), dbActual.toPath(), StandardCopyOption.REPLACE_EXISTING);

                JOptionPane.showMessageDialog(parent,
                        "¡Base de datos restaurada correctamente!",
                        "Restauración Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);

                if (onRestoreSuccess != null) {
                    onRestoreSuccess.run();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "Error al restaurar la base de datos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
