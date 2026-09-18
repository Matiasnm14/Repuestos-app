package com.aeroagro.repuestos.view;

import com.aeroagro.repuestos.controller.RepuestoController;
import com.aeroagro.repuestos.model.entity.Repuesto;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class EditarRepuestoDialog extends JDialog {

    private File nuevaImagenSeleccionada;
    private String rutaImagenActual;
    private final JLabel lblRutaImagen;

    public EditarRepuestoDialog(Frame parent, Repuesto repuesto, List<TipoRepuesto> tipos,
                                RepuestoController controller, Runnable onSuccess) {
        super(parent, "Editar Repuesto", true);
        setSize(480, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        this.rutaImagenActual = repuesto.getRuta();

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<TipoRepuesto> comboTipos = new JComboBox<>();
        tipos.forEach(comboTipos::addItem);

        // Seleccionar el tipo que tenía asignado actualmente
        for (int i = 0; i < comboTipos.getItemCount(); i++) {
            if (comboTipos.getItemAt(i).getId().equals(repuesto.getTipoRepuestoId())) {
                comboTipos.setSelectedIndex(i);
                break;
            }
        }

        JTextField txtFecha = new JTextField(repuesto.getFecha());
        JTextField txtHoras = new JTextField(String.valueOf(repuesto.getHoras()));
        JTextField txtNumParte = new JTextField(repuesto.getNumParte());
        JTextField txtNumSerie = new JTextField(repuesto.getNumSerie());

        JButton btnSeleccionarImagen = new JButton("Cambiar Imagen...");

        // Mostrar nombre de la imagen actual si existe
        String nombreImagenInicial = (rutaImagenActual != null && !rutaImagenActual.isEmpty())
                ? new File(rutaImagenActual).getName()
                : "Sin imagen asignada";

        lblRutaImagen = new JLabel(nombreImagenInicial);
        lblRutaImagen.setFont(new Font("SansSerif", Font.ITALIC, 11));

        btnSeleccionarImagen.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                nuevaImagenSeleccionada = chooser.getSelectedFile();
                lblRutaImagen.setText(nuevaImagenSeleccionada.getName());
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

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.addActionListener(e -> {
            try {
                TipoRepuesto tipoSel = (TipoRepuesto) comboTipos.getSelectedItem();
                if (tipoSel == null) throw new IllegalArgumentException("Seleccione un tipo de repuesto.");

                int horas = Integer.parseInt(txtHoras.getText().trim());

                // Si seleccionó una nueva imagen, guardarla físicamente
                if (nuevaImagenSeleccionada != null) {
                    rutaImagenActual = guardarImagenLocalmente(nuevaImagenSeleccionada);
                }

                controller.actualizarRepuesto(
                        repuesto.getId(),
                        repuesto.getAvionId(),
                        tipoSel.getId(),
                        txtFecha.getText().trim(),
                        horas,
                        txtNumParte.getText().trim(),
                        txtNumSerie.getText().trim(),
                        rutaImagenActual
                );

                JOptionPane.showMessageDialog(this, "Repuesto actualizado correctamente.");
                onSuccess.run();
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Las horas deben ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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