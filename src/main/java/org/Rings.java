package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileReader;

public class Rings extends Product {
    public void viewMenuRings() {
        try {
            JSONArray combinedArray = getCombinedProductArray("src/resources/json/Ringsproducts.json");

            System.out.println(
                    "========================================================================================================================");
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", "Product ID", "Product Name",
                    "Color",
                    "Priceper Pair",
                    "Priceper Side", "Stored Item");
            System.out.println(
                    "========================================================================================================================");

            for (Object obj : combinedArray) {
                JSONObject product = (JSONObject) obj;
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
        } catch (Exception e) {
            e.printStackTrace(); // Display the stack trace for debugging
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private JSONArray getCombinedProductArray(String filePath) throws Exception {
        JSONArray earringsArray = (JSONArray) new JSONParser()
                .parse(new FileReader("src/resources/json/Earringsproducts.json"));
        JSONArray ringsArray = (JSONArray) new JSONParser().parse(new FileReader(filePath));

        JSONArray combinedArray = new JSONArray();
        combinedArray.addAll(earringsArray);
        combinedArray.addAll(ringsArray);

        return combinedArray;
    }
}
