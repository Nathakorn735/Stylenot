package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ProductSearch {

    private String productID;
    private Scanner scanner; // เพิ่ม Scanner

    public ProductSearch() {
        this.scanner = new Scanner(System.in); // กำหนด Scanner
    }

    public boolean searchProductByID(String productID, String productFile) {
        this.productID = productID;
        try {
            JSONArray productsArray = readJSONArrayFromFile(productFile);

            for (Object obj : productsArray) {
                JSONObject product = (JSONObject) obj;
                if (product.get("productID").equals(productID)) {
                    displayProductDetails(product);
                    return true;
                }
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return false;
    }

    private JSONArray readJSONArrayFromFile(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(filePath)) {
            return (JSONArray) parser.parse(fileReader);
        }
    }

    private void displayProductDetails(JSONObject product) {
        printTableHeader();

        // Check if the product matches the searched productID
        if (product.get("productID").equals(productID)) {
            displayProductRow(product);
        } else {
            System.out.println("Product not found with ID: " + productID);
        }

        printTableFooter();
    }

    private void displayProductRow(JSONObject product) {
        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-12s |%n",
                product.get("productID"),
                product.get("productName"),
                product.get("color"),
                product.get("price"),
                product.get("storedItem"));
    }

    private void printTableHeader() {
        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-12s |%n",
                "ProductID", "ProductName", "Color", "Price", "StoredItem");
        System.out.println(
                "======================================================================================================");
    }

    private void printTableFooter() {
        System.out.println(
                "======================================================================================================");
    }

    public void displaySortedPrices(String productFile) {
        try {
            JSONArray productsArray = readJSONArrayFromFile(productFile);

            // Convert JSONArray to a list for sorting
            List<JSONObject> productList = (List<JSONObject>) productsArray;

            // Ask the user for sorting order
            boolean ascendingOrder = getUserSortingOrder();

            // Sort the list based on the "price" property
            Collections.sort(productList, Comparator.comparing(product -> (Double) product.get("price")));

            // If the order is descending, reverse the list
            if (!ascendingOrder) {
                Collections.reverse(productList);
            }

            // Display the sorted prices
            printTableHeader();
            for (JSONObject product : productList) {
                displayProductPrice(product);
            }
            printTableFooter();

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private boolean getUserSortingOrder() {
        boolean validInput = false;
        boolean ascendingOrder = true; // Default order

        while (!validInput) {
            try {
                System.out.println("How do you want to display prices?");
                System.out.println("1. Ascending order (from low to high)");
                System.out.println("2. Descending order (from high to low)");

                int choice = scanner.nextInt();

                // Check if the choice is 1 or 2
                if (choice == 1) {
                    ascendingOrder = true;
                    validInput = true;
                } else if (choice == 2) {
                    ascendingOrder = false;
                    validInput = true;
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
                scanner.next(); // Clear the buffer
            }
        }

        return ascendingOrder;
    }

    private void displayProductPrice(JSONObject product) {
        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-12s |%n",
                product.get("productID"),
                product.get("productName"),
                product.get("color"),
                product.get("price"),
                product.get("storedItem"));
    }
}
