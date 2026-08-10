package com.aeroagro.repuestos.view;

import com.aeroagro.repuestos.controller.AvionController;
import com.aeroagro.repuestos.model.entity.Avion;

import javax.swing.*;
import java.awt.*;

public class EditarAvionDialog extends JDialog {

    public EditarAvionDialog(Frame parent, AvionController controller, Avion avion, Runnable onSuccess) {
        super(parent, "Editar Avión", true);
        setSize(350, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblPlaca = new JLabel("Matrícula / Placa:");
        JTextField txtPlaca = new JTextField(avion.getPlaca());

        formPanel.add(lblPlaca);
        formPanel.add(txtPlaca);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            try {
                controller.actualizarAvion(avion.getId(), txtPlaca.getText());
                JOptionPane.showMessageDialog(this, "Avión actualizado correctamente.");
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
