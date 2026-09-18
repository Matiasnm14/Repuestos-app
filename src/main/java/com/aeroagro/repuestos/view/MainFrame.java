package com.aeroagro.repuestos.view;



import com.aeroagro.repuestos.controller.AvionController;
import com.aeroagro.repuestos.controller.BackupService;
import com.aeroagro.repuestos.controller.RepuestoController;
import com.aeroagro.repuestos.controller.TipoRepuestoController;
import com.aeroagro.repuestos.model.entity.Avion;
import com.aeroagro.repuestos.model.entity.Repuesto;
import com.aeroagro.repuestos.model.entity.TipoRepuesto;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private final AvionController avionController = new AvionController();
    private final TipoRepuestoController tipoController = new TipoRepuestoController();
    private final RepuestoController repuestoController = new RepuestoController();

    private DefaultListModel<Avion> avionListModel;
    private JList<Avion> avionJList;

    private JComboBox<Object> comboFiltroTipo;
    private JTable tablaRepuestos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscar;
    private Avion avionSeleccionado;

    private List<Repuesto> repuestosActuales = new ArrayList<>();

    public MainFrame() {
        initComponents();
        setupContextMenuAviones();
        setupContextMenuRepuestos();
        setupContextMenuTiposRepuesto();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión de Repuestos Aeronáuticos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ================= PANEL IZQUIERDO (LISTA DE AVIONES) =================
        avionListModel = new DefaultListModel<>();
        avionJList = new JList<>(avionListModel);
        avionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        avionJList.setFixedCellHeight(35);

        avionJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                avionSeleccionado = avionJList.getSelectedValue();
                if (avionSeleccionado != null) {
                    cargarRepuestosTabla();
                }
            }
        });

        JScrollPane scrollAviones = new JScrollPane(avionJList);

        JButton btnNuevoAvion = new JButton("+ Nuevo Avión");
        btnNuevoAvion.addActionListener(e ->
                new NuevoAvionDialog(this, avionController, this::cargarAviones).setVisible(true)
        );

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 600));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Aviones Registrados"));
        leftPanel.add(scrollAviones, BorderLayout.CENTER);
        leftPanel.add(btnNuevoAvion, BorderLayout.SOUTH);

        // ================= PANEL DERECHO (FILTROS Y TABLA DE REPUESTOS) =================
        comboFiltroTipo = new JComboBox<>();
        comboFiltroTipo.addActionListener(e -> {
            if (avionSeleccionado != null) {
                cargarRepuestosTabla();
            }
        });

        JButton btnNuevoTipo = new JButton("+ Crear Tipo Repuesto");
        btnNuevoTipo.addActionListener(e ->
                new NuevoTipoRepuestoDialog(this, tipoController, this::cargarTiposRepuesto).setVisible(true)
        );

        JButton btnBackup = new JButton("💾 Crear Backup");
        btnBackup.addActionListener(e -> BackupService.realizarCopiaSeguridad(this));

        JButton btnRestaurar = new JButton("📂 Cargar Backup");
        btnRestaurar.addActionListener(e -> BackupService.restaurarCopiaSeguridad(this, () -> {
            cargarAviones();
            cargarTiposRepuesto();
            if (tableModel != null) {
                tableModel.setRowCount(0);
            }
            avionSeleccionado = null;
        }));



        txtBuscar = new JTextField(20);
        // Filtrar en tiempo real al escribir en el buscador
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { cargarRepuestosTabla(); }
            @Override public void removeUpdate(DocumentEvent e) { cargarRepuestosTabla(); }
            @Override public void changedUpdate(DocumentEvent e) { cargarRepuestosTabla(); }
        });

        JPanel topFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        topFilterPanel.add(new JLabel("Tipo:"));
        topFilterPanel.add(comboFiltroTipo);
        topFilterPanel.add(btnNuevoTipo);
        topFilterPanel.add(new JSeparator(JSeparator.VERTICAL));
        topFilterPanel.add(btnBackup);
        topFilterPanel.add(btnRestaurar);
//        topFilterPanel.add(new JLabel("Buscar (N° Serie/Parte):"));
//        topFilterPanel.add(txtBuscar);


        String[] columnas = {"Tipo Repuesto", "Fecha", "N° Parte", "N° Serie", "Horas de Uso"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRepuestos = new JTable(tableModel);
        tablaRepuestos.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tablaRepuestos);

        JButton btnRegistrarRepuesto = new JButton("Registrar Nuevo Repuesto en Avión");
        btnRegistrarRepuesto.addActionListener(e -> {
            if (avionSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un avión de la lista izquierda.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
            new NuevoRepuestoDialog(this, avionSeleccionado.getId(), tipos, repuestoController, this::cargarRepuestosTabla).setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomPanel.add(new JLabel("Buscar (N° Serie/Parte):"));
        bottomPanel.add(txtBuscar);
        bottomPanel.add(btnRegistrarRepuesto);


        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Repuestos del Avión"));
        rightPanel.add(topFilterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollTabla, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        cargarAviones();
        cargarTiposRepuesto();
    }

    // ================= MENÚS CONTEXTUALES (CLIC DERECHO) =================

    private void setupContextMenuAviones() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Editar Avión");
        JMenuItem deleteItem = new JMenuItem("Eliminar Avión");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Accion Editar
        editItem.addActionListener(e -> {
            Avion selected = avionJList.getSelectedValue();
            if (selected != null) {
                new EditarAvionDialog(this, avionController, selected, this::cargarAviones).setVisible(true);
            }
        });

        deleteItem.addActionListener(e -> {
            Avion selected = avionJList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea eliminar el avión " + selected + "?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try{
                        avionController.eliminarAvion(selected.getId());
                        avionSeleccionado = null;
                        tableModel.setRowCount(0);
                        cargarAviones();
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        avionJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = avionJList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        avionJList.setSelectedIndex(index);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void setupContextMenuRepuestos() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewImageItem = new JMenuItem("Ver Imagen");
        JMenuItem editItem = new JMenuItem("Editar Repuesto");
        JMenuItem deleteItem = new JMenuItem("Eliminar Repuesto");

        popupMenu.add(viewImageItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Acción Ver Imagen
        viewImageItem.addActionListener(e -> {
            int selectedRow = tablaRepuestos.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un repuesto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = tablaRepuestos.convertRowIndexToModel(selectedRow);
            Repuesto repuesto = repuestosActuales.get(modelRow);

            String rutaImagen = repuesto.getRuta();
            if (rutaImagen == null || rutaImagen.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Este repuesto no tiene una imagen asignada.", "Sin Imagen", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            java.io.File archivoImg = new java.io.File(rutaImagen);
            if (!archivoImg.exists()) {
                JOptionPane.showMessageDialog(this, "No se encontró el archivo de imagen en la ruta:\n" + rutaImagen, "Archivo No Encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new VerImagenDialog(this, rutaImagen, "N° Serie: " + repuesto.getNumSerie()).setVisible(true);
        });

        // Acción Editar
        editItem.addActionListener(e -> {
            int selectedRow = tablaRepuestos.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un repuesto de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = tablaRepuestos.convertRowIndexToModel(selectedRow);
            Repuesto repuesto = repuestosActuales.get(modelRow);

            List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
            new EditarRepuestoDialog(this, repuesto, tipos, repuestoController, this::cargarRepuestosTabla).setVisible(true);
        });

        // Acción Eliminar
        deleteItem.addActionListener(e -> {
            int selectedRow = tablaRepuestos.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = tablaRepuestos.convertRowIndexToModel(selectedRow);
                Repuesto repuesto = repuestosActuales.get(modelRow);

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea eliminar el repuesto con N° Serie " + repuesto.getNumSerie() + "?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        repuestoController.eliminarRepuesto(repuesto.getId());
                        cargarRepuestosTabla();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        tablaRepuestos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tablaRepuestos.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        tablaRepuestos.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void setupContextMenuTiposRepuesto() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Editar Tipo Seleccionado");
        JMenuItem deleteItem = new JMenuItem("Eliminar Tipo Seleccionado");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        // Accion Editar
        editItem.addActionListener(e -> {
            Object seleccionado = comboFiltroTipo.getSelectedItem();
            if (seleccionado instanceof TipoRepuesto tipo) {
                new EditarTipoRepuestoDialog(this, tipoController, tipo, () -> {
                    System.out.println("Editar Repuesto");
                    cargarTiposRepuesto();
                    cargarRepuestosTabla();
                }).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de repuesto válido para editar.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteItem.addActionListener(e -> {
            Object seleccionado = comboFiltroTipo.getSelectedItem();
            if (seleccionado instanceof TipoRepuesto tipo) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea eliminar el tipo de repuesto '" + tipo.getNombre() + "'?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try{
                        tipoController.eliminarTipoRepuesto(tipo.getId());
                        cargarTiposRepuesto();
                        cargarRepuestosTabla();
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo de repuesto válido para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        });

        comboFiltroTipo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void cargarAviones() {
        avionListModel.clear();
        List<Avion> lista = avionController.listarAviones();
        lista.forEach(avionListModel::addElement);
    }

    private void cargarTiposRepuesto() {
        comboFiltroTipo.removeAllItems();
        comboFiltroTipo.addItem("TODOS");
        List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
        tipos.forEach(comboFiltroTipo::addItem);
    }

    public void cargarRepuestosTabla() {
        tableModel.setRowCount(0);
        if (avionSeleccionado == null) return;

        String tipoId = null;
        Object seleccionado = comboFiltroTipo.getSelectedItem();
        if (seleccionado instanceof TipoRepuesto) {
            tipoId = ((TipoRepuesto) seleccionado).getId();
        }

        List<Repuesto> lista = repuestoController.filtrarRepuestos(avionSeleccionado.getId(), tipoId);

        if (lista != null) {
            lista.sort(Comparator.comparing(Repuesto::getFecha, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

            String busqueda = txtBuscar.getText().trim().toLowerCase();
            if (!busqueda.isEmpty()) {
                lista = lista.stream()
                        .filter(r -> (r.getNumParte() != null && r.getNumParte().toLowerCase().contains(busqueda))
                                || (r.getNumSerie() != null && r.getNumSerie().toLowerCase().contains(busqueda)))
                        .collect(Collectors.toList());
            }

            repuestosActuales = lista;

            for (Repuesto r : repuestosActuales) {
                TipoRepuesto tipoRepuesto = tipoController.obtenerPorId(r.getTipoRepuestoId()).orElse(null);
                String nombreTipo = (tipoRepuesto != null) ? tipoRepuesto.getNombre() : "Sin tipo";

                tableModel.addRow(new Object[]{
                        nombreTipo,
                        r.getFecha(),
                        r.getNumParte(),
                        r.getNumSerie(),
                        r.getHoras()
                });
            }
        }
    }

    public void init() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}