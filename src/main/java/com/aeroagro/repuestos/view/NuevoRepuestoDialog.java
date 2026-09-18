package com.aeroagro.repuestos.view;

import com.aeroagro.repuestos.controller.RepuestoController;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class NuevoRepuestoDialog extends JDialog {

    private File imagenSeleccionada;
    private final JLabel lblRutaImagen;

    public NuevoRepuestoDialog(Frame parent, String avionId, List<TipoRepuesto> tipos,
                               RepuestoController controller, Runnable onSuccess) {
        super(parent, "Registrar Repuesto", true);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<TipoRepuesto> comboTipos = new JComboBox<>();
        tipos.forEach(comboTipos::addItem);

        JTextField txtFecha = new JTextField();
        txtFecha.setText(LocalDate.now().toString());
        JTextField txtHoras = new JTextField();
        JTextField txtNumParte = new JTextField();
        JTextField txtNumSerie = new JTextField();

        JButton btnSeleccionarImagen = new JButton("Seleccionar Imagen...");
        lblRutaImagen = new JLabel("Ninguna imagen seleccionada");
        lblRutaImagen.setFont(new Font("SansSerif", Font.ITALIC, 11));

        btnSeleccionarImagen.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                imagenSeleccionada = chooser.getSelectedFile();
                lblRutaImagen.setText(imagenSeleccionada.getName());
            }
        });

        formPanel.add(new JLabel("Tipo de Repuesto:"));
        formPanel.add(comboTipos);
        formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"));
        formPanel.add(txtFecha);
        formPanel.add(new JLabel("Horas de Uso:"));
        formPanel.add(txtHoras);
        formPanel.add(new JLabel("N° de Parte:"));
        formPanel.add(txtNumParte);
        formPanel.add(new JLabel("N° de Serie:"));
        formPanel.add(txtNumSerie);
        formPanel.add(new JLabel("Imagen:"));
        formPanel.add(btnSeleccionarImagen);
        formPanel.add(new JLabel(""));
        formPanel.add(lblRutaImagen);

        JButton btnGuardar = new JButton("Guardar Repuesto");
        btnGuardar.addActionListener(e -> {
            try {
                TipoRepuesto tipoSel = (TipoRepuesto) comboTipos.getSelectedItem();
                if (tipoSel == null) throw new IllegalArgumentException("Debe seleccionar un tipo de repuesto.");

                int horas = Integer.parseInt(txtHoras.getText().trim());
                String rutaImagenGuardada = guardarImagenLocalmente(imagenSeleccionada);

                controller.registrarRepuesto(
                        avionId,
                        tipoSel.getId(),
                        txtFecha.getText().trim(),
                        horas,
                        txtNumParte.getText().trim(),
                        txtNumSerie.getText().trim(),
                        rutaImagenGuardada
                );

                JOptionPane.showMessageDialog(this, "Repuesto registrado correctamente.");
                onSuccess.run();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Las horas deben ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private String guardarImagenLocalmente(File archivoOrigen) throws IOException {
        if (archivoOrigen == null) return null;

        String userHome = System.getProperty("user.home");
        File directorioDestino = new File(userHome + File.separator + ".repuestos_app" + File.separator + "imagenes_repuestos");

        if (!directorioDestino.exists()) {
            directorioDestino.mkdirs();
        }

        String nombreArchivo = System.currentTimeMillis() + "_" + archivoOrigen.getName();
        File destino = new File(directorioDestino, nombreArchivo);

        Files.copy(archivoOrigen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return destino.getAbsolutePath();
    }
}