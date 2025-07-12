import java.util.*;

/*
    ⚡ This is a Cart Management System implemented using SOLID Principles.
    ⚡ We have two types of products: Fractional and Physical.
    ⚡ The cart implementation varies depending on the product type.
    ⚡ Each SOLID principle is demonstrated in the appropriate context.
*/


// ====================== Interface Segregation Principle =========================

abstract class Product {
    protected String name;
    protected int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public abstract String getDisplayInfo();
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

abstract class Cart {
    protected final List<String> items = new ArrayList<>();
    protected double total = 0;

    public abstract void addItem(Product product);

    public void updateItem(String name, Product newProduct) {
        removeItem(name);
        addItem(newProduct);
    }

    public void removeItem(String name) {
        items.removeIf(item -> item.startsWith(name));
    }

    public List<String> getList() {
        return items;
    }

    public double getTotal() {
        return total;
    }
}

// Interfaces are segregated based on the type of product being handled
interface PhysicalCartIF {
    int getTotalUnits();
}

interface FractionalCartIF {
    double getTotalGrams();
}

// ===================================================================================



// ====================== Single Responsibility Principle ============================

// Each class below has a single responsibility:
// - FractionalProduct handles weight-based product data
// - PhysicalProduct handles unit-based product data

class FractionalProduct extends Product {
    private double weight;

    public FractionalProduct(String name, int price, double weight) {
        super(name, price);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String getDisplayInfo() {
        return name + " (" + weight + "g)";
    }
}

class PhysicalProduct extends Product {
    private int units;

    public PhysicalProduct(String name, int price, int units) {
        super(name, price);
        this.units = units;
    }

    public int getUnits() {
        return units;
    }

    @Override
    public String getDisplayInfo() {
        return name + " (" + units + " units)";
    }
}

// ===================================================================================



// ============== Open/Closed & Single Responsibility Principles =====================

// ✅ Open/Closed: We extend functionality via new classes and interfaces
//    without modifying the existing Cart abstraction.
// ✅ SRP: Each class has one clear responsibility related to a product type.

class PhysicalProductCart extends Cart implements PhysicalCartIF {
    private int totalUnits = 0;

    @Override
    public void addItem(Product product) {
        if (!(product instanceof PhysicalProduct)) return;

        PhysicalProduct p = (PhysicalProduct) product;
        totalUnits += p.getUnits();
        items.add(p.getDisplayInfo());
        total += p.getPrice() * p.getUnits();
    }

    @Override
    public int getTotalUnits() {
        return totalUnits;
    }
}

class FractionalProductCart extends Cart implements FractionalCartIF {
    private double totalGrams = 0;

    @Override
    public void addItem(Product product) {
        if (!(product instanceof FractionalProduct)) return;

        FractionalProduct p = (FractionalProduct) product;
        totalGrams += p.getWeight();
        items.add(p.getDisplayInfo());
        total += p.getPrice() * p.getWeight();
    }

    @Override
    public double getTotalGrams() {
        return totalGrams;
    }
}

// ===================================================================================



// ======================== Dependency Inversion Principle ===========================

// CartService (high-level module) depends on the Cart abstraction,
// not on concrete implementations like PhysicalProductCart or FractionalProductCart.
// This allows us to swap implementations easily.

class CartService {
    private final Cart cart;

    public CartService(Cart cart) {
        this.cart = cart;
    }

    public void checkout() {
        System.out.println("🛒 Cart Items:");
        for (String item : cart.getList()) {
            System.out.println(" - " + item);
        }
        System.out.println("💵 Total: $" + cart.getTotal());
    }
}

// ===================================================================================



public class Main {
    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();

        products.add(new PhysicalProduct("Bullion Bar 10g", 5, 2));
        products.add(new PhysicalProduct("Bullion Bar 5g", 3, 3));
        products.add(new FractionalProduct("Gold Dust", 1, 25.5));
        products.add(new FractionalProduct("Silver Shavings", 0.5, 50));

        Cart physicalCart = new PhysicalProductCart();
        for (Product p : products) {
            if (p instanceof PhysicalProduct) {
                physicalCart.addItem(p);
            }
        }

        // ✅ Liskov Substitution Principle:
        // CartService uses Cart type and works correctly with any Cart subclass.
        CartService cartService = new CartService(physicalCart);
        cartService.checkout();

        System.out.println("---------");

        Cart fractionalCart = new FractionalProductCart();
        for (Product p : products) {
            if (p instanceof FractionalProduct) {
                fractionalCart.addItem(p);
            }
        }

        CartService fractionalService = new CartService(fractionalCart);
        fractionalService.checkout();
    }
}
