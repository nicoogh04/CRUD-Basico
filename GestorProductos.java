// Importamos los componentes principales de Swing.
import javax.swing.*;

// Importamos DefaultTableModel y componentes para el filtrado en tiempo real.
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Importamos clases para organizar los componentes gráficos e imágenes.
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
    private JTextField txtBuscar; // Campo de texto para la búsqueda en tiempo real


    // ========================================================
    // COMPONENTES DE LA TABLA Y FILTRADO
    // ========================================================

    private JTable tabla;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter; // Permite filtrar los productos dinámicamente


    // ========================================================
    // COMPONENTES PARA ESTADÍSTICAS Y TOTALES
    // ========================================================

    private JLabel lblTotal;
    private JLabel lblTotalProductos;
    private JLabel lblTotalUnidades;
    private JLabel lblValorIntermedio; 


    // ========================================================
    // CONSTRUCTOR
    // ========================================================

    public GestorProductos() {
        setTitle("Gestor de Productos");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearInterfaz();
    }


    // ========================================================
    // CREAR INTERFAZ
    // ========================================================

    private void crearInterfaz() {

        // Panel con formulario de entrada
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ----------------------------------------------------
        // CAMPO NOMBRE
        // ----------------------------------------------------
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // ----------------------------------------------------
        // CAMPO PRECIO
        // ----------------------------------------------------
        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        // ----------------------------------------------------
        // CAMPO STOCK
        // ----------------------------------------------------
        panelFormulario.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelFormulario.add(txtStock);

        // ----------------------------------------------------
        // CATEGORÍA
        // ----------------------------------------------------
        panelFormulario.add(new JLabel("Categoría:"));
        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Almacén");
        cmbCategoria.addItem("Bebidas");
        cmbCategoria.addItem("Limpieza");
        cmbCategoria.addItem("Verduleria");
        cmbCategoria.addItem("Otros");
        panelFormulario.add(cmbCategoria);

        // ----------------------------------------------------
        // BOTONES DEL FORMULARIO
        // ----------------------------------------------------
        JButton btnAgregar = new JButton("Agregar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelFormulario.add(btnAgregar);
        panelFormulario.add(btnLimpiar);


        // ====================================================
        // CREAR TABLA Y SISTEMA DE FILTRADO
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

        // Asignamos el TableRowSorter a la JTable para habilitar la ordenación y búsqueda
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tabla);


        // ----------------------------------------------------
        // CAMPO DE BÚSQUEDA EN TIEMPO REAL
        // ----------------------------------------------------
        txtBuscar = new JTextField();

        // Escuchamos los cambios de texto letra por letra
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarProductos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarProductos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarProductos();
            }
        });

        // Panel superior para colocar la etiqueta "Buscar:" junto al campo de texto
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 10));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        panelBusqueda.add(new JLabel("Buscar:"), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);


        // ====================================================
        // BOTÓN ELIMINAR CON ICONO DE TACHITO
        // ====================================================

        ImageIcon iconoOriginal = new ImageIcon("papelera.png");
        Image imagenRedimensionada = iconoOriginal.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon iconoTachito = new ImageIcon(imagenRedimensionada);

        JButton btnEliminar = new JButton("Eliminar", iconoTachito);
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);


        // ====================================================
        // ETIQUETAS DE ESTADÍSTICAS Y TOTALES
        // ====================================================

        lblTotalProductos = new JLabel("Productos: 0");
        lblTotalUnidades = new JLabel("Unidades: 0");
        lblValorIntermedio = new JLabel("Valor Intermedio: $0.00");
        lblTotal = new JLabel("Valor total del stock: $0.00");


        // ====================================================
        // EVENTOS DE LOS BOTONES
        // ====================================================

        btnAgregar.addActionListener(e -> agregarProducto());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarProducto());


        // ====================================================
        // ORGANIZAR PANEL INFERIOR
        // ====================================================

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Subpanel a la izquierda con FlowLayout para organizar el botón y las tres estadísticas
        JPanel panelIzquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelIzquierda.add(btnEliminar);
        panelIzquierda.add(lblTotalProductos);
        panelIzquierda.add(lblTotalUnidades);
        panelIzquierda.add(lblValorIntermedio);

        panelInferior.add(panelIzquierda, BorderLayout.WEST);
        panelInferior.add(lblTotal, BorderLayout.EAST);


        // ====================================================
        // CONFIGURAR VENTANA Y PANELES
        // ====================================================

        setLayout(new BorderLayout());

        // Panel central que agrupa el buscador y la tabla
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelBusqueda, BorderLayout.NORTH);
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        add(panelFormulario, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
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
            JOptionPane.showMessageDialog(
                    this,
                    "Debe ingresar el nombre del producto.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            txtNombre.requestFocus();
            return;
        }

        double precio;
        int stock;

        try {
            precio = Double.parseDouble(precioTexto.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El precio debe ser un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            txtPrecio.requestFocus();
            return;
        }

        try {
            stock = Integer.parseInt(stockTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El stock debe ser un número entero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            txtStock.requestFocus();
            return;
        }

        if (precio <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "El precio debe ser mayor que cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (stock < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "El stock no puede ser negativo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Producto producto = new Producto(nombre, precio, stock, categoria);

        modelo.addRow(new Object[] {
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                String.format("%.2f", producto.getValorStock()).replace(",", ".")
        });

        actualizarTotal();
        limpiarFormulario();

        JOptionPane.showMessageDialog(
                this,
                "Producto agregado correctamente.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE
        );
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
    // ELIMINAR PRODUCTO (COMPATIBLE CON BUSCADOR)
    // ========================================================

    private void eliminarProducto() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un producto.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el producto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            // Convertimos el índice visual al índice real del modelo para evitar borrar datos incorrectos al estar filtrado
            int filaModelo = tabla.convertRowIndexToModel(filaVista);
            modelo.removeRow(filaModelo);
            actualizarTotal();
        }
    }


    // ========================================================
    // FILTRAR PRODUCTOS EN TIEMPO REAL
    // ========================================================

    private void filtrarProductos() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            sorter.setRowFilter(null); // Si no hay texto, se muestran todas las filas
        } else {
            // Búsqueda insensible a mayúsculas/minúsculas en la columna 0 (Nombre)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 0));
        }
    }


    // ========================================================
    // ACTUALIZAR ESTADÍSTICAS Y TOTALES
    // ========================================================

    private void actualizarTotal() {
        int totalProductos = modelo.getRowCount();
        int totalUnidades = 0;
        double valorIntermedio = 0;
        double total = 0;

        for (int i = 0; i < totalProductos; i++) {
            double precio = Double.parseDouble(modelo.getValueAt(i, 1).toString().replace(",", "."));
            int stock = Integer.parseInt(modelo.getValueAt(i, 2).toString());
            double valor = Double.parseDouble(modelo.getValueAt(i, 4).toString().replace(",", "."));

            totalUnidades += stock;
            valorIntermedio += precio;
            total += valor;
        }

        if (totalProductos > 0) {
            valorIntermedio = valorIntermedio / totalProductos;
        }

        lblTotalProductos.setText("Productos: " + totalProductos);
        lblTotalUnidades.setText("Unidades: " + totalUnidades);
        lblValorIntermedio.setText("Valor Intermedio: $" + String.format("%.2f", valorIntermedio));
        lblTotal.setText(String.format("Valor total del stock: $%.2f", total));
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