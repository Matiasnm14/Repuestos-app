package com.aeroagro.repuestos.view;

import com.aeroagro.repuestos.controller.AvionController;
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
import java.util.stream.Collectors;

public class MainFrame2 extends JFrame {

    private final AvionController avionController = new AvionController();
    private final TipoRepuestoController tipoController = new TipoRepuestoController();
    private final RepuestoController repuestoController = new RepuestoController();

    private DefaultListModel<Avion> avionListModel;
    private JList<Avion> avionJList;

    private JComboBox<Object> comboFiltroTipo;
    private JTextField txtBuscar; // Campo de búsqueda por N° Serie / N° Parte
    private JTable tablaRepuestos;
    private DefaultTableModel tableModel;

    private Avion avionSeleccionado;
    private List<Repuesto> repuestosActuales = new ArrayList<>();

    public MainFrame2() {
        initComponents();
        setupContextMenuAviones();
        setupContextMenuRepuestos();
        setupContextMenuTiposRepuesto();
    }

    private void initComponents() {
        setTitle("Sistema de Gestión de Repuestos Aeronáuticos");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ================= PANEL IZQUIERDO =================
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

        // ================= PANEL DERECHO =================
        // 1. Panel Superior (Filtros y Buscador)
        comboFiltroTipo = new JComboBox<>();
        comboFiltroTipo.addActionListener(e -> {
            if (avionSeleccionado != null) {
                cargarRepuestosTabla();
            }
        });

        JButton btnNuevoTipo = new JButton("+ Crear Tipo");
        btnNuevoTipo.addActionListener(e ->
                new NuevoTipoRepuestoDialog(this, tipoController, this::cargarTiposRepuesto).setVisible(true)
        );

        txtBuscar = new JTextField(15);
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
        topFilterPanel.add(new JLabel("Buscar (N° Serie/Parte):"));
        topFilterPanel.add(txtBuscar);

        // 2. Tabla de Repuestos
        String[] columnas = {"Tipo", "Fecha", "N° Parte", "N° Serie", "Horas de Uso"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRepuestos = new JTable(tableModel);
        tablaRepuestos.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tablaRepuestos);

        // 3. Botón Registrar
        JButton btnRegistrarRepuesto = new JButton("Registrar Nuevo Repuesto en Avión");
        btnRegistrarRepuesto.addActionListener(e -> {
            if (avionSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un avión de la lista izquierda.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
            new NuevoRepuestoDialog(this, avionSeleccionado.getId(), tipos, repuestoController, this::cargarRepuestosTabla).setVisible(true);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
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

    public void cargarRepuestosTabla() {
        tableModel.setRowCount(0);
        if (avionSeleccionado == null) return;

        String tipoId = null;
        Object seleccionado = comboFiltroTipo.getSelectedItem();
        if (seleccionado instanceof TipoRepuesto) {
            tipoId = ((TipoRepuesto) seleccionado).getId();
        }

        // 1. Cargar repuestos desde la BD
        List<Repuesto> lista = repuestoController.filtrarRepuestos(avionSeleccionado.getId(), tipoId);

        if (lista != null) {
            // 2. Ordenar por fecha (más reciente a más antiguo)
            lista.sort(Comparator.comparing(Repuesto::getFecha, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

            // 3. Filtrar por N° Serie o N° Parte
            String busqueda = txtBuscar.getText().trim().toLowerCase();
            if (!busqueda.isEmpty()) {
                lista = lista.stream()
                        .filter(r -> (r.getNumParte() != null && r.getNumParte().toLowerCase().contains(busqueda))
                                || (r.getNumSerie() != null && r.getNumSerie().toLowerCase().contains(busqueda)))
                        .collect(Collectors.toList());
            }

            repuestosActuales = lista;

            // 4. Llenar la tabla
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

    public void cargarAviones() {
        avionListModel.clear();
        List<Avion> lista = avionController.listarAviones();
        if (lista != null) {
            lista.forEach(avionListModel::addElement);
        }
    }

    public void cargarTiposRepuesto() {
        comboFiltroTipo.removeAllItems();
        comboFiltroTipo.addItem("TODOS");
        List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
        if (tipos != null) {
            tipos.forEach(comboFiltroTipo::addItem);
        }
    }

    private void setupContextMenuAviones() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Editar Avión");
        JMenuItem deleteItem = new JMenuItem("Eliminar Avión");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

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
                        "¿Desea eliminar el avión " + selected + " y sus repuestos asociados?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        avionController.eliminarAvion(selected.getId());
                        avionSeleccionado = null;
                        tableModel.setRowCount(0);
                        cargarAviones();
                    } catch (Exception ex) {
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
        JMenuItem editItem = new JMenuItem("Editar Repuesto");
        JMenuItem deleteItem = new JMenuItem("Eliminar Repuesto");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        editItem.addActionListener(e -> {
            int selectedRow = tablaRepuestos.getSelectedRow();
            if (selectedRow < 0) return;

            int modelRow = tablaRepuestos.convertRowIndexToModel(selectedRow);
            Repuesto repuesto = repuestosActuales.get(modelRow);

            List<TipoRepuesto> tipos = tipoController.listarTiposRepuesto();
            new EditarRepuestoDialog(this, repuesto, tipos, repuestoController, this::cargarRepuestosTabla).setVisible(true);
        });

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

        editItem.addActionListener(e -> {
            Object seleccionado = comboFiltroTipo.getSelectedItem();
            if (seleccionado instanceof TipoRepuesto tipo) {
                new EditarTipoRepuestoDialog(this, tipoController, tipo, () -> {
                    cargarTiposRepuesto();
                    cargarRepuestosTabla();
                }).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo válido para editar.", "Atención", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteItem.addActionListener(e -> {
            Object seleccionado = comboFiltroTipo.getSelectedItem();
            if (seleccionado instanceof TipoRepuesto tipo) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea eliminar el tipo '" + tipo.getNombre() + "' y sus repuestos asociados?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        tipoController.eliminarTipoRepuesto(tipo.getId());
                        cargarTiposRepuesto();
                        cargarRepuestosTabla();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un tipo válido para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
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

    public void init() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}