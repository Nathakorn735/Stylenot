package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.Scanner;

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

            System.out.println("ProductID " + productID + " not found in " + productFile);
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
        System.out.println("ProductID: " + product.get("productID"));
        System.out.println("ProductName: " + product.get("productName"));
        System.out.println("Color: " + product.get("color"));
        System.out.println("Price: " + product.get("price"));
        System.out.println("StoredItem: " + product.get("storedItem"));
    }
}
