package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductSearch {
    public void searchProductByID(String productID, String productFile) {
        try {
            JSONArray productsArray = readJSONArrayFromFile(productFile);

            for (Object obj : productsArray) {
                JSONObject product = (JSONObject) obj;
                if (product.get("productID").equals(productID)) {
                    displayProductDetails(product);
                    return;
                }
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private JSONArray readJSONArrayFromFile(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(filePath)) {
            return (JSONArray) parser.parse(fileReader);
        }
    }

    private void displayProductDetails(JSONObject product) {
        printTableHeader();
        displayProductDetailsFromJsonArray("src/resources/json/Ringsproducts.json");
        displayProductDetailsFromJsonArray("src/resources/json/Earringsproducts.json");
        printTableFooter();
    }

    private void displayProductDetailsFromJsonArray(String filePath) {
        try {
            JSONArray jsonArray = readJSONArrayFromFile(filePath);
            for (Object obj : jsonArray) {
                JSONObject product = (JSONObject) obj;
                displayProductRow(product);
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
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

            // Sort the list based on the "price" property
            Collections.sort(productList, Comparator.comparing(product -> (Double) product.get("price")));

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

    private void displayProductPrice(JSONObject product) {
        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-12s |%n",
                product.get("productID"),
                product.get("productName"),
                product.get("color"),
                product.get("price"),
                product.get("storedItem"));
    }
}
