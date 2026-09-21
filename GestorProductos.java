import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

// ============================================================
// CLASE PRINCIPAL
// ============================================================

public class GestorProductos extends JFrame {

    // ========================================================
    // COMPONENTES DEL FORMULARIO
    // ========================================================

    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JComboBox<String> cmbCategoria;

    // Campo de búsqueda
    private JTextField txtBuscar;

    // Guarda la fila seleccionada en la tabla para edición.
    private int filaSeleccionada = -1;

    // ========================================================
    // COMPONENTES DE LA TABLA
    // ========================================================

    private JTable tabla;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    // ========================================================
    // COMPONENTES DE ESTADÍSTICAS Y TOTALES
    // ========================================================

    private JLabel lblCantidadProductos;
    private JLabel lblTotalUnidades;
    private JLabel lblPrecioPromedio; // "Valor intermedio"
    private JLabel lblTotal;          // "Valor total del stock"

    // ========================================================
    // CONSTRUCTOR
    // ========================================================

    public GestorProductos() {

        setTitle("Gestor de Productos");
        setSize(1050, 600); // Ventana más ancha para alojar montos grandes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
    }

    // ========================================================
    // CREAR INTERFAZ
    // ========================================================

    private void crearInterfaz() {

        JPanel panelSuperior = new JPanel(new BorderLayout());

        // ----------------------------------------------------
        // PANEL FORMULARIO
        // ----------------------------------------------------
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelFormulario.add(txtStock);

        panelFormulario.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Almacén");
        cmbCategoria.addItem("Bebidas");
        cmbCategoria.addItem("Limpieza");
        cmbCategoria.addItem("Verduleria");
        cmbCategoria.addItem("Otros");
        panelFormulario.add(cmbCategoria);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnLimpiar = new JButton("Limpiar");
        panelFormulario.add(btnAgregar);
        panelFormulario.add(btnLimpiar);

        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        // ----------------------------------------------------
        // BARRA DE BÚSQUEDA
        // ----------------------------------------------------
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        
        JLabel lblBuscar = new JLabel("Buscar:");
        txtBuscar = new JTextField();

        panelBusqueda.add(lblBuscar, BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);

        // ====================================================
        // CREAR TABLA
        // ====================================================

        String[] columnas = {
                "Nombre",
                "Precio",
                "Stock",
                "Categoría",
                "Valor Stock"
        };

        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tabla);

        // ====================================================
        // BOTONES DE ACCIÓN
        // ====================================================

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(220, 53, 69)); 
        btnEliminar.setForeground(Color.WHITE);             
        btnEliminar.setFocusPainted(false);

        try {
            btnEliminar.setIcon(new ImageIcon(getClass().getResource("/papelera.png")));
            btnEliminar.setIconTextGap(8);
        } catch (Exception e) {
            // Carga segura del icono
        }

        JButton btnEditar = new JButton("Editar");
        try {
            btnEditar.setIcon(new ImageIcon(getClass().getResource("/lapiz.png")));
            btnEditar.setIconTextGap(8);
        } catch (Exception e) {
            // Carga segura del icono
        }

        JButton btnGuardar = new JButton("Guardar Cambios");

        // ====================================================
        // ETIQUETAS DE ESTADÍSTICAS Y TOTALES
        // ====================================================

        lblCantidadProductos = new JLabel("Productos: 0");
        lblTotalUnidades = new JLabel("Unidades: 0");
        lblPrecioPromedio = new JLabel("Valor intermedio: $0.00");
        lblTotal = new JLabel("Valor total del stock: $0.00");

        Font fuenteNegrita = new Font("SansSerif", Font.BOLD, 12);
        lblCantidadProductos.setFont(fuenteNegrita);
        lblTotalUnidades.setFont(fuenteNegrita);
        lblPrecioPromedio.setFont(fuenteNegrita);
        lblTotal.setFont(fuenteNegrita);

        // ====================================================
        // EVENTOS
        // ====================================================

        btnAgregar.addActionListener(e -> agregarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnGuardar.addActionListener(e -> guardarCambios());

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String texto = txtBuscar.getText().trim();
                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });

        // ====================================================
        // PANEL INFERIOR (CORREGIDO PARA EVITAR RECORTES)
        // ====================================================

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 15));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelBotones.add(btnEliminar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnGuardar);

        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        panelStats.add(lblCantidadProductos);
        panelStats.add(lblTotalUnidades);
        panelStats.add(lblPrecioPromedio);
        panelStats.add(lblTotal);

        panelInferior.add(panelBotones, BorderLayout.WEST);
        panelInferior.add(panelStats, BorderLayout.CENTER); // Se asigna CENTER para darle todo el espacio sobrante

        // ====================================================
        // CONFIGURAR VENTANA
        // ====================================================

        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // ========================================================
    // AGREGAR PRODUCTO
    // ========================================================

    private void agregarProducto() {

        String nombre = txtNombre.getText().trim();
        String precioTexto = txtPrecio.getText().trim();
        String stockTexto = txtStock.getText().trim();
        String categoria = cmbCategoria.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del producto.", "Error", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        double precio;
        int stock;

        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }

        try {
            stock = Integer.parseInt(stockTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            txtStock.requestFocus();
            return;
        }

        if (precio <= 0) {
            JOptionPane.showMessageDialog(this, "El precio debe ser mayor que cero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (stock < 0) {
            JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Producto producto = new Producto(nombre, precio, stock, categoria);

        // Formato para PRECIO y VALOR STOCK eliminando notación científica
        modelo.addRow(new Object[] {
                producto.getNombre(),
                String.format("%.2f", producto.getPrecio()).replace(",", "."),
                producto.getStock(),
                producto.getCategoria(),
                String.format("%.2f", producto.getValorStock()).replace(",", ".")
        });

        actualizarEstadisticas();
        limpiarFormulario();

        JOptionPane.showMessageDialog(this, "Producto agregado correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================================================
    // LIMPIAR FORMULARIO
    // ========================================================

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    // ========================================================
    // ELIMINAR PRODUCTO
    // ========================================================

    private void eliminarProducto() {

        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            int filaModelo = tabla.convertRowIndexToModel(filaVista);
            modelo.removeRow(filaModelo);
            actualizarEstadisticas();
        }
    }

    // ========================================================
    // EDITAR PRODUCTO
    // ========================================================

    private void editarProducto() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        filaSeleccionada = tabla.convertRowIndexToModel(filaVista);

        txtNombre.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
        txtPrecio.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
        txtStock.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
        cmbCategoria.setSelectedItem(modelo.getValueAt(filaSeleccionada, 3).toString());
    }

    // ========================================================
    // GUARDAR CAMBIOS
    // ========================================================

    private void guardarCambios() {
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Primero seleccione el producto y presione el botón 'Editar'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String nombre = txtNombre.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String categoria = cmbCategoria.getSelectedItem().toString();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del producto.", "Error", JOptionPane.ERROR_MESSAGE);
                txtNombre.requestFocus();
                return;
            }

            if (precio <= 0) {
                JOptionPane.showMessageDialog(this, "El precio debe ser mayor que cero.", "Error", JOptionPane.ERROR_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }

            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                txtStock.requestFocus();
                return;
            }

            Producto x = new Producto(nombre, precio, stock, categoria);

            modelo.setValueAt(x.getNombre(), filaSeleccionada, 0);
            modelo.setValueAt(String.format("%.2f", x.getPrecio()).replace(",", "."), filaSeleccionada, 1);
            modelo.setValueAt(x.getStock(), filaSeleccionada, 2);
            modelo.setValueAt(x.getCategoria(), filaSeleccionada, 3);
            modelo.setValueAt(String.format("%.2f", x.getValorStock()).replace(",", "."), filaSeleccionada, 4);

            limpiarFormulario();
            actualizarEstadisticas();
            filaSeleccionada = -1;

            JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los cambios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ========================================================
    // ACTUALIZAR ESTADÍSTICAS Y TOTALES
    // ========================================================

    private void actualizarEstadisticas() {

        double totalValorStock = 0;
        int totalUnidades = 0;
        double sumaPrecios = 0;
        int cantidadProductos = modelo.getRowCount();

        for (int i = 0; i < cantidadProductos; i++) {
            double precio = Double.parseDouble(modelo.getValueAt(i, 1).toString().replace(",", "."));
            int stock = Integer.parseInt(modelo.getValueAt(i, 2).toString());
            
            String valorTexto = modelo.getValueAt(i, 4).toString().replace(",", ".");
            double valorStock = Double.parseDouble(valorTexto);

            sumaPrecios += precio;
            totalUnidades += stock;
            totalValorStock += valorStock;
        }

        double precioPromedio = cantidadProductos > 0 ? (sumaPrecios / cantidadProductos) : 0;

        lblCantidadProductos.setText("Productos: " + cantidadProductos);
        lblTotalUnidades.setText("Unidades: " + totalUnidades);
        lblPrecioPromedio.setText(String.format("Valor intermedio: $%.2f", precioPromedio).replace(",", "."));
        lblTotal.setText(String.format("Valor total del stock: $%.2f", totalValorStock).replace(",", "."));
    }

    // ========================================================
    // MÉTODO MAIN
    // ========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GestorProductos ventana = new GestorProductos();
            ventana.setVisible(true);
        });
    }
}