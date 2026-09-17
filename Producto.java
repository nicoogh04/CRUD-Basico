// ============================================================
// CLASE PRODUCTO
// ============================================================

// Esta clase representa un producto del comercio.
public class Producto {

    // Atributos privados del producto.
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;

    // Constructor: permite crear un Producto con sus datos iniciales.
    public Producto(String nombre, double precio, int stock, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Devuelve el nombre del producto.
    public String getNombre() {
        return nombre;
    }

    // Devuelve el precio del producto.
    public double getPrecio() {
        return precio;
    }

    // Devuelve el stock disponible.
    public int getStock() {
        return stock;
    }

    // Devuelve la categoría.
    public String getCategoria() {
        return categoria;
    }

    // Calcula el valor total del stock de este producto.
    public double getValorStock() {
        return precio * stock;
    }
}
