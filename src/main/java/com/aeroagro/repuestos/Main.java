package com.aeroagro.repuestos;

import com.aeroagro.repuestos.controller.AvionController;
import com.aeroagro.repuestos.controller.RepuestoController;
import com.aeroagro.repuestos.controller.TipoRepuestoController;
import com.aeroagro.repuestos.db.DatabaseConnection;
import com.aeroagro.repuestos.view.MainFrame;
import com.aeroagro.repuestos.view.MainFrame2;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            DatabaseConnection.initDatabase(); // Inicializa las tablas antes de crear la vista
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.init();
        });
    }
}