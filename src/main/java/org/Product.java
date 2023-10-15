package main.java.org;

public class Product {
    public void viewMenu() {
        try {

            System.out.println(
                    "======================================================================================================");
            System.out.format("| %-10s | %-40s | %-10s | %-14s | %-10s |\n", "Product ID", "Product Name",
                    "Color",
                    "Price", "Stored Item");
            System.out.println(
                    "======================================================================================================");

        } catch (Exception e) {

            System.out.println("An error occurred: " + e.getMessage());
        }
    }

}
