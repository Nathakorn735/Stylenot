package main.java.org;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Cashier extends User {
    private static final String EARRINGS_FILE = "src/resources/json/Earringsproducts.json";
    private static final String RINGS_FILE = "src/resources/json/Ringsproducts.json";
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";
    private static final int COLUMNS = 3;

    public Cashier(String userId, String username, String password, String firstName, String lastName) {
        super(userId, username, password, firstName, lastName);
    }

    private static JSONObject createProduct(String productID, int quantity) {
        JSONObject product = new JSONObject();
        product.put("productID", productID);
        product.put("quantity", quantity);
        return product;
    }

    private static void saveReceiptToFile(JSONObject receipt) throws IOException {
        JSONArray receipts;
        try {
            receipts = readJSONArrayFromFile(RECEIPT_FILE);
        } catch (Exception e) {
            receipts = new JSONArray();
        }

        receipts.add(receipt);

        try (FileWriter fileWriter = new FileWriter(RECEIPT_FILE)) {
            fileWriter.write(receipts.toJSONString());
        }
    }

    private static int generateReceiptId() {
        try {
            JSONArray receipts = readJSONArrayFromFile(RECEIPT_FILE);
            if (!receipts.isEmpty()) {
                JSONObject lastReceipt = (JSONObject) receipts.get(receipts.size() - 1);
                int lastReceiptId = Integer.parseInt(lastReceipt.get("receiptId").toString());
                return lastReceiptId + 1;
            } else {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
    }

    private static boolean removeStoredItemByID(JSONArray jsonArray, String productID, int quantity) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            if (product.get("productID").equals(productID)) {
                int storedItem = Integer.parseInt(product.get("storedItem").toString());
                if (storedItem >= quantity) {
                    product.put("storedItem", storedItem - quantity);

                    try {
                        saveJSONArrayToFile(jsonArray, getProductFile(productID));
                    } catch (Exception e) {
                        System.out.println("Error saving product details: " + e.getMessage());
                    }

                    return true;
                } else {
                    System.out.println("Insufficient quantity to order for ProductID " + productID);
                    return false;
                }
            }
        }
        return false;
    }

    private static String getProductFile(String productID) {
        if (productID.startsWith("E")) {
            return EARRINGS_FILE;
        } else {
            return RINGS_FILE;
        }
    }

    private static void orderProduct(JSONArray selectedProducts, String productType, String productFile) {
        try {
            boolean continueOrdering = true;
            JSONArray productsArray = readJSONArrayFromFile(productFile);

            do {
                System.out.println(
                        "======================================================================================================");
                System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", "Product ID", "Product Name",
                        "Color", "Price", "Stored Item");
                System.out.println(
                        "======================================================================================================");
                if (productsArray.isEmpty()) {
                    System.out.println("No " + productType + " found.");
                    return;
                }

                displayProductList(productsArray);
                System.out.println(
                        "======================================================================================================");
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter the productID to Order: ");
                String productIDToOrder = scanner.next();
                System.out.print("Enter the quantity to Order: ");
                int quantityToOrder = scanner.nextInt();

                if (!removeStoredItemByID(productsArray, productIDToOrder, quantityToOrder)) {
                    System.out.println("ProductID not found or insufficient quantity.");
                } else {
                    int receiptId = generateReceiptId();
                    JSONObject orderedProduct = createProduct(productIDToOrder, quantityToOrder);

                    boolean productExists = false;
                    for (Object obj : selectedProducts) {
                        JSONObject existingProduct = (JSONObject) obj;
                        if (existingProduct.get("productID").equals(productIDToOrder)) {
                            int existingQuantity = Integer.parseInt(existingProduct.get("quantity").toString());
                            existingProduct.put("quantity", existingQuantity + quantityToOrder);
                            productExists = true;
                            break;
                        }
                    }

                    if (!productExists) {
                        selectedProducts.add(orderedProduct);
                    }

                    saveJSONArrayToFile(productsArray, productFile);
                    System.out.println(productType + " Order successfully!");
                }
                System.out.print(
                        "Do you want to order another " + productType + "? (Type 'N' to exit, 'Y' to continue): ");
                String userInput = scanner.next();
                if (userInput.equalsIgnoreCase("N")) {
                    continueOrdering = false;
                }
            } while (continueOrdering);

            displaySelectedProducts(selectedProducts, "Selected " + productType, productFile);
            createReceipt(selectedProducts);

        } catch (Exception e) {
            System.out.println("Error ordering " + productType + ": " + e.getMessage());
        }
    }

    private static void saveReceiptToFile(JSONObject receipt, String filename) throws IOException {
        JSONArray receipts;
        try {
            receipts = readJSONArrayFromFile(filename);
        } catch (Exception e) {
            receipts = new JSONArray();
        }

        receipts.add(receipt);

        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(receipts.toJSONString());
        }
    }

    private static void createReceipt(JSONArray selectedProducts) {
        try {
            int receiptId = generateReceiptId();

            JSONObject receipt = new JSONObject();
            receipt.put("receiptId", receiptId);
            receipt.put("datetime", new Date().toString());
            receipt.put("products", selectedProducts);

            saveReceiptToFile(receipt, RECEIPT_FILE);

            System.out.println("Receipt created successfully. Receipt ID: " + receiptId);
        } catch (Exception e) {
            System.out.println("Error creating receipt: " + e.getMessage());
        }
    }

    public static void orderEarring() {
        JSONArray selectedEarrings = new JSONArray();
        orderProduct(selectedEarrings, "Earrings", EARRINGS_FILE);
    }

    public static void orderRing() {
        JSONArray selectedRings = new JSONArray();
        orderProduct(selectedRings, "Rings", RINGS_FILE);
    }

    private static void displaySelectedProducts(JSONArray selectedProducts, String category, String productFile) {
        System.out.println(
                "======================================================================================================");
        System.out.println("Selected " + category + ":");
        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s | %-20s |\n", "Product ID", "Product Name",
                "Color", "Price", "Quantity", "Datetime");
        System.out.println(
                "======================================================================================================");

        for (Object obj : selectedProducts) {
            JSONObject selectedProduct = (JSONObject) obj;
            String productID = selectedProduct.get("productID").toString();
            String quantity = selectedProduct.get("quantity").toString();

            try {
                JSONArray productsArray = readJSONArrayFromFile(productFile);
                for (Object productObj : productsArray) {
                    JSONObject product = (JSONObject) productObj;
                    if (product.get("productID").equals(productID)) {
                        String productName = product.get("productName").toString();
                        String color = product.get("color").toString();
                        String price = product.get("price").toString();
                        String datetime = new Date().toString();
                        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-10s | %-20s |\n", productID, productName,
                                color, price, quantity, datetime);
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error fetching product details: " + e.getMessage());
            }
        }

        System.out.println(
                "======================================================================================================");
    }

    private static void displayAllProducts(String fileName) {
        try {
            JSONArray productsArray = readProducts(fileName);

            System.out.println("Current products details:");

            System.out.println(
                    "======================================================================================================");
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", "Product ID", "Product Name",
                    "Color", "Price", "Stored Item");
            System.out.println(
                    "======================================================================================================");

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
                    "======================================================================================================");

        } catch (IOException | ParseException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static void displayProductDetails(JSONObject product) {
        String productID = product.get("productID").toString();
        String productName = product.get("productName").toString();
        String color = product.get("color").toString();
        String price = product.get("price").toString();
        int storedItem = Integer.parseInt(product.get("storedItem").toString());

        System.out.printf("| %-10s | %-40s | %-10s | %-15s | %-10d |\n", productID, productName,
                color, price, storedItem);
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
            System.out.printf("| %-10s | %-40s | %-10s | %-15s| %-12s|%n", product.get("productID"),
                    product.get("productName"),
                    product.get("color"), product.get("price"),
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



