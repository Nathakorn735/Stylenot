package main.java.org;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Cashier extends User {
    private static final String EARRINGS_FILE = "src/resources/json/Earringsproducts.json";
    private static final String RINGS_FILE = "src/resources/json/Ringsproducts.json";
    private static final int COLUMNS = 3;

    public Cashier(String userId, String username, String password, String firstName, String lastName) {
        super(userId, username, password, firstName, lastName);
    }

    private static boolean removeStoredItemByID(JSONArray jsonArray, String productID, int quantity) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            if (product.get("productID").equals(productID)) {
                int storedItem = Integer.parseInt(product.get("storedItem").toString());
                if (storedItem >= quantity) {
                    product.put("storedItem", storedItem - quantity);
                    return true;
                } else {
                    System.out.println("Insufficient quantity to order for ProductID " + productID);
                    return false;
                }
            }
        }
        return false;
    }

    private static boolean removeProductByID(JSONArray jsonArray, String productID) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject product = (JSONObject) jsonArray.get(i);
            if (product.get("productID").equals(productID)) {
                jsonArray.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void orderEarring() {
        try {
            boolean continueOrdering = true;

            do {
                JSONArray earringsArray = readJSONArrayFromFile(EARRINGS_FILE);
                System.out.println(
                        "========================================================================================================================");
                System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", "Product ID", "Product Name",
                        "Color",
                        "Priceper Pair",
                        "Priceper Side", "Stored Item");
                System.out.println(
                        "========================================================================================================================");
                if (earringsArray.isEmpty()) {
                    System.out.println("No Earrings found.");
                    return;
                }

                displayProductList(earringsArray);
                System.out.println(
                        "========================================================================================================================");

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the productID to Order: ");
                String productIDToOrder = scanner.next();
                System.out.print("Enter the quantity to Order: ");
                int quantityToOrder = scanner.nextInt();

                if (!removeStoredItemByID(earringsArray, productIDToOrder, quantityToOrder)) {
                    System.out.println("ProductID not found or insufficient quantity.");
                } else {
                    saveJSONArrayToFile(earringsArray, EARRINGS_FILE);
                    System.out.println("Earring Order successfully!");
                }

                System.out.print("Do you want to order another earring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueOrdering = false;
                }
            } while (continueOrdering);

        } catch (Exception e) {
            System.out.println("Error ordering Earring: " + e.getMessage());
        }
    }

    public static void orderRing() {
        try {
            boolean continueOrdering = true;

            do {
                JSONArray ringsArray = readJSONArrayFromFile(RINGS_FILE);
                System.out.println(
                        "========================================================================================================================");
                System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", "Product ID", "Product Name",
                        "Color",
                        "Priceper Pair",
                        "Priceper Side", "Stored Item");
                System.out.println(
                        "========================================================================================================================");
                if (ringsArray.isEmpty()) {
                    System.out.println("No Rings found.");
                    return;
                }

                displayProductList(ringsArray);
                System.out.println(
                        "========================================================================================================================");

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the productID to Order: ");
                String productIDToOrder = scanner.next();
                System.out.print("Enter the quantity to Order: ");
                int quantityToOrder = scanner.nextInt();

                if (!removeStoredItemByID(ringsArray, productIDToOrder, quantityToOrder)) {
                    System.out.println("ProductID not found or insufficient quantity.");
                } else {
                    saveJSONArrayToFile(ringsArray, RINGS_FILE);
                    System.out.println("Ring Order successfully!");
                }

                System.out.print("Do you want to order another ring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueOrdering = false;
                }
            } while (continueOrdering);

        } catch (Exception e) {
            System.out.println("Error ordering Ring: " + e.getMessage());
        }
    }

    private static void displayAllProducts(String fileName) {
        try {
            JSONArray productsArray = readProducts(fileName);

            System.out.println("Current products details:");

            System.out.println(
                    "========================================================================================================================");
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", "Product ID", "Product Name",
                    "Color",
                    "Priceper Pair",
                    "Priceper Side", "Stored Item");
            System.out.println(
                    "========================================================================================================================");

            int count = 0;
            for (Object obj : productsArray) {
                JSONObject product = (JSONObject) obj;
                displayProductDetails(product);
                count++;

                if (count % COLUMNS == 0) {
                    System.out.println();
                }
            }
            System.out.println(
                    "========================================================================================================================");

        } catch (IOException | ParseException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayProductDetails(JSONObject product) {
        String productID = product.get("productID").toString();
        String productName = product.get("productName").toString();
        String color = product.get("color").toString();
        String priceperPair = product.get("priceperPair").toString();
        String priceperSide = product.get("priceperSide").toString();
        String storedItem = product.get("storedItem").toString();

        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-15s | %-10s |\n", productID, productName,
                color, priceperPair, priceperSide, storedItem);
    }

    private static JSONArray readProducts(String fileName) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(fileName)) {
            Object obj = jsonParser.parse(fileReader);
            return (JSONArray) obj;
        }
    }

    private static void writeProducts(String fileName, JSONArray productsArray) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(productsArray.toJSONString());
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

    private static void displayProductList(JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-15s | %-12s|%n", product.get("productID"),
                    product.get("productName"),
                    product.get("color"), product.get("priceperPair"), product.get("priceperSide"),
                    product.get("storedItem"));
        }
    }

    private static boolean removeProductByID(JSONArray jsonArray, String productID) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            if (product.get("productID").equals(productID)) {
                jsonArray.remove(obj);
                return true;
            }
        }
        return false;
    }

    private static void saveJSONArrayToFile(JSONArray jsonArray, String filename) throws Exception {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(jsonArray.toJSONString());
        }
    }
}