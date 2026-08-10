package com.aeroagro.repuestos.view;



import com.aeroagro.repuestos.controller.AvionController;

import javax.swing.*;
import java.awt.*;

public class NuevoAvionDialog extends JDialog {

    public NuevoAvionDialog(Frame parent, AvionController controller, Runnable onSuccess) {
        super(parent, "Registrar Nuevo Avión", true);
        setSize(350, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblPlaca = new JLabel("Matrícula / Placa:");
        JTextField txtPlaca = new JTextField();

        formPanel.add(lblPlaca);
        formPanel.add(txtPlaca);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                controller.registrarAvion(txtPlaca.getText());
                JOptionPane.showMessageDialog(this, "Avión registrado con éxito.");
                onSuccess.run(); // Recarga la lista en la pantalla principal
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);
    }
}
