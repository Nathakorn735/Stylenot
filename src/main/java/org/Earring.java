package main.java.org;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Earring extends Product {

    public Earring() {
        // Constructor สำหรับคลาส Earring
    }

    public void viewMenuEarrings() {
        JSONArray combinedArray = loadProductData("src/resources/json/Earringsproducts.json");

        System.out.println(
                "========================================================================================================================");
        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", "Product ID", "Product Name", "Color",
                "Priceper Pair", "Priceper Side", "Stored Item");
        System.out.println(
                "========================================================================================================================");

        for (Object obj : combinedArray) {
            JSONObject product = (JSONObject) obj;
            printProductDetails(product);
        }
    }

    // เพิ่ม method สำหรับโหลดข้อมูลสินค้า
    private JSONArray loadProductData(String filePath) {
        try {
            return (JSONArray) new JSONParser().parse(new FileReader(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while loading product data: " + e.getMessage());
            return new JSONArray();
        }
    }

    // เพิ่ม method สำหรับพิมพ์รายละเอียดสินค้า
    private void printProductDetails(JSONObject product) {
        String productID = (String) product.get("productID");
        String productName = (String) product.get("productName");
        String color = (String) product.get("color");

        // Handle the casting appropriately
        Double priceperPair = null;
        Double priceperSide = null;
        Long storedItem = null;

        try {
            Object priceperPairObj = product.get("priceperPair");
            if (priceperPairObj != null) {
                priceperPair = ((Number) priceperPairObj).doubleValue();
            }

            Object priceperSideObj = product.get("priceperSide");
            if (priceperSideObj != null) {
                priceperSide = ((Number) priceperSideObj).doubleValue();
            }

            Object storedItemObj = product.get("storedItem");
            if (storedItemObj != null) {
                storedItem = ((Number) storedItemObj).longValue();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Error casting values: " + e.getMessage());
        }

        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", productID, productName, color,
                priceperPair, priceperSide, storedItem);
    }
}
