package main.java.org;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Manager extends User {
    private static final String EARRINGS_FILE = "src/resources/json/Earringsproducts.json";
    private static final String RINGS_FILE = "src/resources/json/Ringsproducts.json";
    private static final int COLUMNS = 3;

    public Manager(String userId, String username, String password, String firstName, String lastName,
            double salary) {
        super(userId, username, password, firstName, lastName);
    }

    public static void addEarring() {
        Scanner scanner = new Scanner(System.in);
    
        try {
            boolean continueAdding = true;
    
            do {
                System.out.println("Enter Earring details:");
                String productID = generateProductID(EARRINGS_FILE);
                System.out.print("Product Name: ");
                String productName = scanner.next();
    
                System.out.print("Color: ");
                String color = scanner.next();
    
                System.out.print("Price per Pair: ");
                double priceperPair = scanner.nextDouble();
    
                System.out.print("Price per Side: ");
                double priceperSide = scanner.nextDouble();
    
                System.out.print("StoredItem: ");
                int storedItem = scanner.nextInt();
    
                JSONObject earringData = new JSONObject();
                earringData.put("productID", productID);
                earringData.put("productName", productName);
                earringData.put("color", color);
                earringData.put("priceperPair", priceperPair);
                earringData.put("priceperSide", priceperSide);
                earringData.put("storedItem", storedItem);
    
                JSONArray earringsArray;
                try (FileReader fileReader = new FileReader(EARRINGS_FILE)) {
                    int data;
                    StringBuilder content = new StringBuilder();
    
                    while ((data = fileReader.read()) != -1) {
                        content.append((char) data);
                    }
    
                    if (content.length() == 0) {
                        earringsArray = new JSONArray();
                    } else {
                        earringsArray = (JSONArray) new JSONParser().parse(content.toString());
                    }
                }
    
                earringsArray.add(earringData);
    
                try (FileWriter fileWriter = new FileWriter(EARRINGS_FILE)) {
                    fileWriter.write(earringsArray.toJSONString());
                }
    
                System.out.println("Earring added successfully!");
    
                System.out.print("Do you want to add another earring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueAdding = false;
                }
            } while (continueAdding);
    
        } catch (Exception e) {
            System.out.println("Error adding Earring: " + e.getMessage());
        } finally {
            scanner.close();
        }
    
        // Display all products after exiting the loop
        displayAllProducts(EARRINGS_FILE);
    }
    
    
    public static void addRing() {
        Scanner scanner = new Scanner(System.in);
    
        try {
            boolean continueAdding = true;
    
            do {
                System.out.println("Enter Ring details:");
                String productID = generateProductID(RINGS_FILE);
                System.out.print("Product Name: ");
                String productName = scanner.next();
    
                System.out.print("Color: ");
                String color = scanner.next();
    
                System.out.print("Price per Pair: ");
                double priceperPair = scanner.nextDouble();
    
                System.out.print("Price per Side: ");
                double priceperSide = scanner.nextDouble();
    
                System.out.print("StoredItem: ");
                int storedItem = scanner.nextInt();
    
                JSONObject ringData = new JSONObject();
                ringData.put("productID", productID);
                ringData.put("productName", productName);
                ringData.put("color", color);
                ringData.put("priceperPair", priceperPair);
                ringData.put("priceperSide", priceperSide);
                ringData.put("storedItem", storedItem);
    
                JSONArray ringsArray;
                try (FileReader fileReader = new FileReader(RINGS_FILE)) {
                    int data;
                    StringBuilder content = new StringBuilder();
    
                    while ((data = fileReader.read()) != -1) {
                        content.append((char) data);
                    }
    
                    if (content.length() == 0) {
                        ringsArray = new JSONArray();
                    } else {
                        ringsArray = (JSONArray) new JSONParser().parse(content.toString());
                    }
                }
    
                ringsArray.add(ringData);
    
                try (FileWriter fileWriter = new FileWriter(RINGS_FILE)) {
                    fileWriter.write(ringsArray.toJSONString());
                }
    
                System.out.println("Ring added successfully!");
    
                System.out.print("Do you want to add another ring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueAdding = false;
                }
            } while (continueAdding);
    
        } catch (Exception e) {
            System.out.println("Error adding Ring: " + e.getMessage());
        } finally {
            scanner.close();
        }
        displayAllProducts(EARRINGS_FILE);
    }
    

    private static String generateProductID(String filename) {
        try {
            JSONArray productsArray;
            try (FileReader fileReader = new FileReader(filename)) {
                StringBuilder content = new StringBuilder();
                int data;

                while ((data = fileReader.read()) != -1) {
                    content.append((char) data);
                }

                if (content.length() == 0) {
                    productsArray = new JSONArray();
                } else {
                    // Parse the content into JSONArray
                    productsArray = (JSONArray) new JSONParser().parse(content.toString());
                }
            }

            int productCount = productsArray.size();
            String prefix = filename.equals(EARRINGS_FILE) ? "K" : "R";
            return prefix + String.format("%02d", productCount + 1);

        } catch (Exception e) {
            System.out.println("Error generating product ID: " + e.getMessage());
            return "";
        }
    }

    public static void deleteEarring() {
        try {
            boolean continueDeleting = true;
    
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
    
                System.out.println("Earrings List:");
                displayProductList(earringsArray);
    
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the productID to delete: ");
                String productIDToDelete = scanner.next();
    
                if (!removeProductByID(earringsArray, productIDToDelete)) {
                    System.out.println("ProductID not found.");
                } else {
                    saveJSONArrayToFile(earringsArray, EARRINGS_FILE);
                    System.out.println("Earring deleted successfully!");
                }
    
                System.out.print("Do you want to delete another earring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueDeleting = false;
                }
            } while (continueDeleting);
    
        } catch (Exception e) {
            System.out.println("Error deleting Earring: " + e.getMessage());
        }
    }
    
    public static void deleteRing() {
        try {
            boolean continueDeleting = true;
    
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
    
                System.out.println("Rings List:");
                displayProductList(ringsArray);
    
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the productID to delete: ");
                String productIDToDelete = scanner.next();
    
                if (!removeProductByID(ringsArray, productIDToDelete)) {
                    System.out.println("ProductID not found.");
                } else {
                    saveJSONArrayToFile(ringsArray, RINGS_FILE);
                    System.out.println("Ring deleted successfully!");
                            displayAllProducts(EARRINGS_FILE);
                }
    
                System.out.print("Do you want to delete another ring? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueDeleting = false;
                }
            } while (continueDeleting);
    
        } catch (Exception e) {
            System.out.println("Error deleting Ring: " + e.getMessage());
        }
    }
    
    public static void displayAllProducts(String fileName) {
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

    public static void editProduct(String fileName) {
        try {
            JSONArray productsArray = readProducts(fileName);
    
            boolean continueEditing = true;
    
            do {
                System.out.print("Enter productID to edit: ");
                Scanner scanner = new Scanner(System.in);
                String productID = scanner.nextLine();
    
                boolean found = false;
    
                for (int i = 0; i < productsArray.size(); i++) {
                    JSONObject product = (JSONObject) productsArray.get(i);
                    if (product.get("productID").equals(productID)) {
                        // Product found, display current details
                        displayProductDetails(product);
    
                        // Prompt for new data
                        System.out.println("Enter new product name: ");
                        String newName = scanner.nextLine();
                        product.put("productName", newName);
    
                        System.out.println("Enter new color: ");
                        String newColor = scanner.nextLine();
                        product.put("color", newColor);
    
                        System.out.println("Enter new price per pair: ");
                        double newPricePerPair = scanner.nextDouble();
                        product.put("priceperPair", newPricePerPair);
    
                        System.out.println("Enter new price per side: ");
                        double newPricePerSide = scanner.nextDouble();
                        product.put("priceperSide", newPricePerSide);
    
                        System.out.println("Enter new stored items: ");
                        int newStoredItem = scanner.nextInt();
                        product.put("storedItem", newStoredItem);
    
                        found = true;
                        break;
                    }
                }
    
                if (found) {
                    writeProducts(fileName, productsArray);
                    System.out.println("Product updated successfully!");
                            displayAllProducts(EARRINGS_FILE);

                } else {
                    System.out.println("Product not found.");
                }
    
                System.out.print("Do you want to edit another product? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueEditing = false;
                }
            } while (continueEditing);
    
        } catch (IOException | ParseException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    

    private static void displayProductDetails(JSONObject product) {
        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-15s | %-12s|%n", product.get("productID"),
                product.get("productName"),
                product.get("color"), product.get("priceperPair"), product.get("priceperSide"),
                product.get("storedItem"));
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