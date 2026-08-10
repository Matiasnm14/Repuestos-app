package com.aeroagro.repuestos.view;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VerImagenDialog extends JDialog {

    public VerImagenDialog(Frame parent, String rutaImagen, String titulo) {
        super(parent, "Imagen de Repuesto - " + titulo, true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        File imgFile = new File(rutaImagen);
        if (!imgFile.exists()) {
            add(new JLabel("El archivo no existe en el disco.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        ImageIcon icon = new ImageIcon(rutaImagen);
        Image image = icon.getImage();

        // Escalar manteniendo proporción si supera el tamaño límite
        int maxWidth = 560;
        int maxHeight = 420;
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (width > maxWidth || height > maxHeight) {
            double ratio = Math.min((double) maxWidth / width, (double) maxHeight / height);
            width = (int) (width * ratio);
            height = (int) (height * ratio);

            Image scaledImg = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImg);
        }

        JLabel labelImagen = new JLabel(icon, SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(labelImagen);
        scrollPane.setBorder(null);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnCerrar);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
