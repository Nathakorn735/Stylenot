package main.java.org;

import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Rings extends Product {

    public void viewMenuRings() {
        try {
            // อ่านข้อมูลจากไฟล์ Ringsproducts.json
            JSONArray ringsArray = readJSONArrayFromFile("src/resources/json/Ringsproducts.json");

            System.out.println("Rings Products Menu:");
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", "Product ID", "Product Name",
                    "Color",
                    "Price", "Stored Item");
            System.out.println(
                    "======================================================================================================");

            displayProductList(ringsArray);

            System.out.println(
                    "======================================================================================================");

        } catch (Exception e) {
            System.out.println("An error occurred while viewing Rings menu: " + e.getMessage());
        }
    }

    // คัดลอกเมธอดนี้จาก Earring class
    private static void displayProductList(JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            System.out.printf("| %-10s | %-40s | %-10s | %-15s| %-12s|%n", product.get("productID"),
                    product.get("productName"),
                    product.get("color"), product.get("price"),
                    product.get("storedItem"));
        }
    }

    // คัดลอกเมธอดนี้จาก Earring class
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
