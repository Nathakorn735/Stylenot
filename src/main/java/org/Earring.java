package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileReader;

import org.json.simple.parser.JSONParser;

public class Earring extends Product {

    public void viewMenuEarrings() {
        try {
            JSONArray earringsArray = readJSONArrayFromFile("src/resources/json/Earringsproducts.json");

            System.out.println("Earrings Products Menu:");

            displayProductList(earringsArray);

            System.out.println(
                    "======================================================================================================");

        } catch (Exception e) {
            System.out.println("An error occurred while viewing Earrings menu: " + e.getMessage());
        }
    }

    private static void displayProductList(JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            System.out.printf("| %-10s | %-40s | %-10s | %-15s| %-12s|%n", product.get("productID"),
                    product.get("productName"),
                    product.get("color"), product.get("price"),
                    product.get("storedItem"));
        }
    }

    private static JSONArray readJSONArrayFromFile(String filename) throws Exception {
        JSONArray jsonArray;
        try (FileReader fileReader = new FileReader(filename)) {
            int data;
            StringBuilder content = new StringBuilder();

            while ((data = fileReader.read()) != -1) {
                content.append((char) data);
            }

            if (content.length() == 0) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = (JSONArray) new JSONParser().parse(content.toString());
            }
        }
        return jsonArray;
    }
}
