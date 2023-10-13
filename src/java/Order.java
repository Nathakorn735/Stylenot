package java;

public class Order {
    protected int stock;
    protected String color;
    protected double price;


    public Order(int stock, String color, double price) {
        this.stock = stock;
        this.color = color;
        this.price = price;
    }


    public int getStock() {
        return stock;
    }


    public void setStock(int stock) {
        this.stock = stock;
    }


    public String getColor() {
        return color;
    }


    public void setColor(String color) {
        this.color = color;
    }


    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    

    
}
