package com.aeroagro.repuestos.view;



import com.aeroagro.repuestos.controller.TipoRepuestoController;

import javax.swing.*;
import java.awt.*;

public class NuevoTipoRepuestoDialog extends JDialog {

    public NuevoTipoRepuestoDialog(Frame parent, TipoRepuestoController controller, Runnable onSuccess) {
        super(parent, "Nuevo Tipo de Repuesto", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        JLabel lblDesc = new JLabel("Descripción:");
        JTextField txtDesc = new JTextField();

        formPanel.add(lblNombre);
        formPanel.add(txtNombre);
        formPanel.add(lblDesc);
        formPanel.add(txtDesc);

        JButton btnGuardar = new JButton("Guardar Categoría");
        btnGuardar.addActionListener(e -> {
            try {
                controller.registrarTipoRepuesto(txtNombre.getText(), txtDesc.getText());
                JOptionPane.showMessageDialog(this, "Tipo de repuesto guardado.");
                onSuccess.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);
    }
}