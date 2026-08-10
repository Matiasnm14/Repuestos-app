package com.aeroagro.repuestos.view;

import com.aeroagro.repuestos.controller.TipoRepuestoController;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import javax.swing.*;
import java.awt.*;

public class EditarTipoRepuestoDialog extends JDialog {

    public EditarTipoRepuestoDialog(Frame parent, TipoRepuestoController controller, TipoRepuesto tipo, Runnable onSuccess) {
        super(parent, "Editar Tipo de Repuesto", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtNombre = new JTextField(tipo.getNombre());
        JTextField txtDesc = new JTextField(tipo.getDescripcion());

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtNombre);
        formPanel.add(new JLabel("Descripción:"));
        formPanel.add(txtDesc);

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            try {
                controller.actualizarTipoRepuesto(tipo.getId(), txtNombre.getText(), txtDesc.getText());
                JOptionPane.showMessageDialog(this, "Tipo de repuesto actualizado correctamente.");
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
