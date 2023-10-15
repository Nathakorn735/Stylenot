package main.java.org;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Cashier extends User {
    private static final String EARRINGS_FILE = "src/resources/json/Earringsproducts.json";
    private static final String RINGS_FILE = "src/resources/json/Ringsproducts.json";
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";
    private static final String PAYMENT_AMOUNT = "paymentAmount";
    private static Scanner scanner = new Scanner(System.in);

    public Cashier(String userId, String username, String password, String firstName, String lastName) {
        super(userId, username, password, firstName, lastName);
    }

    private static JSONObject createProduct(String productID, int quantity, String color, double price,
            String productName, int storedItem) {
        JSONObject product = new JSONObject();
        product.put("productID", productID);
        product.put("quantity", quantity);
        product.put("color", color);
        product.put("price", price);
        product.put("productName", productName);
        product.put("storedItem", storedItem);
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

    private static boolean removeStoredItemByID(JSONArray jsonArray, String productID, int quantity,
            String productType, JSONArray selectedProducts) {
        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            if (product.get("productID").equals(productID)) {
                int storedItem = Integer.parseInt(product.get("storedItem").toString());
                String color = product.get("color").toString();
                double price = Double.parseDouble(product.get("price").toString());
                String productName = product.get("productName").toString();

                if (storedItem >= quantity) {
                    product.put("storedItem", storedItem - quantity);

                    JSONObject orderedProduct = createProduct(productID, quantity, color, price, productName, quantity);
                    selectedProducts.add(orderedProduct);

                    return true;
                } else {
                    System.out.println("Insufficient quantity to order for ProductID " + productID);
                    return false;
                }
            }
        }
        return false;
    }

    private static JSONObject getProductDetails(String productID) {
        try {
            JSONArray earringsArray = readJSONArrayFromFile(EARRINGS_FILE);
            JSONArray ringsArray = readJSONArrayFromFile(RINGS_FILE);

            for (Object obj : earringsArray) {
                JSONObject product = (JSONObject) obj;
                if (product.get("productID").equals(productID)) {
                    JSONObject productDetails = new JSONObject();
                    productDetails.put("color", product.get("color"));
                    productDetails.put("price", Double.parseDouble(product.get("price").toString()));
                    productDetails.put("productName", product.get("productName"));
                    productDetails.put("storedItem", Integer.parseInt(product.get("storedItem").toString()));
                    return productDetails;
                }
            }

            for (Object obj : ringsArray) {
                JSONObject product = (JSONObject) obj;
                if (product.get("productID").equals(productID)) {
                    JSONObject productDetails = new JSONObject();
                    productDetails.put("color", product.get("color"));
                    productDetails.put("price", Double.parseDouble(product.get("price").toString()));
                    productDetails.put("productName", product.get("productName"));
                    productDetails.put("storedItem", Integer.parseInt(product.get("storedItem").toString()));
                    return productDetails;
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting product details: " + e.getMessage());
        }
        return null;
    }

    private static void orderProduct(JSONArray selectedProducts, String productType, String productFile) {
        try {
            boolean continueOrdering = true;
            JSONArray productsArray;

            do {
                productsArray = readJSONArrayFromFile(productFile);

                System.out.println(
                        "======================================================================================================");

                if (productsArray.isEmpty()) {
                    System.out.println("No " + productType + " found.");
                    return;
                }

                displayProductList(productsArray);

                System.out.println(
                        "======================================================================================================");
                System.out.print("Enter the productID to Order: ");
                String productIDToOrder = scanner.next();
                System.out.print("Enter the quantity to Order: ");
                int quantityToOrder = scanner.nextInt();

                JSONObject productDetails = getProductDetails(productIDToOrder);

                if (productDetails == null
                        || !removeStoredItemByID(productsArray, productIDToOrder, quantityToOrder, productType,
                                selectedProducts)) {
                    System.out.println("ProductID not found or insufficient quantity.");
                } else {
                    int receiptId = generateReceiptId();
                    JSONObject orderedProduct = createProduct(productIDToOrder, quantityToOrder,
                            productDetails.get("color").toString(),
                            Double.parseDouble(productDetails.get("price").toString()),
                            productDetails.get("productName").toString(), 0);

                    boolean productExists = false;
                    for (Object obj : selectedProducts) {
                        JSONObject existingProduct = (JSONObject) obj;
                        if (existingProduct.get("productID").equals(productIDToOrder)) {
                            existingProduct.put("quantity",
                                    Integer.parseInt(existingProduct.get("quantity").toString()));
                            productExists = true;
                            break;
                        }
                    }

                    if (!productExists) {
                        selectedProducts.add(orderedProduct);
                    }

                    System.out.println("Product added to the order.");

                    // Create the receipt with a placeholder amountReceived (0.0 for now)
                    createReceipt(selectedProducts, 0.0);

                    System.out.print("Do you want to order another " + productType
                            + "? (Type 'N' to exit, 'Y' to continue): ");
                    String userInput = scanner.next();
                    if (userInput.equalsIgnoreCase("N")) {
                        continueOrdering = false;
                    }
                }
            } while (continueOrdering);

            displaySelectedProducts(selectedProducts, "Selected " + productType, productFile);
            double amountReceived = getAmountReceived(calculateTotalPrice(selectedProducts));
            createReceipt(selectedProducts, amountReceived);

        } catch (Exception e) {
            System.out.println("Error ordering " + productType + ": " + e.getMessage());
        }
    }

    private static void createReceipt(JSONArray selectedProducts, double amountReceived) {
        try {
            int receiptId = generateReceiptId();
            JSONObject receipt = new JSONObject();
            receipt.put("receiptId", receiptId);
            receipt.put("date", getCurrentDate());
            receipt.put("time", getCurrentTime());

            JSONArray productsArray = new JSONArray();
            double totalPrice = 0.0;

            for (Object obj : selectedProducts) {
                JSONObject product = (JSONObject) obj;

                double price = Double.parseDouble(product.get("price").toString());
                int quantity = Integer.parseInt(product.get("quantity").toString());
                double totalProductPrice = price * quantity;

                JSONObject productDetails = new JSONObject();
                productDetails.put("productID", product.get("productID"));
                productDetails.put("productName", product.get("productName"));
                productDetails.put("color", product.get("color"));
                productDetails.put("price", price);
                productDetails.put("quantity", quantity);
                productDetails.put("totalPrice", totalProductPrice);

                productsArray.add(productDetails);

                totalPrice += totalProductPrice;
            }

            receipt.put("products", productsArray);
            receipt.put("totalPrice", totalPrice);
            receipt.put("amountReceived", amountReceived);

            double change = amountReceived - totalPrice;
            receipt.put("change", change);

            saveReceiptToFile(receipt);

            System.out.println("Receipt created successfully!");

        } catch (Exception e) {
            System.out.println("Error creating receipt: " + e.getMessage());
        }
    }

    private static void displaySelectedProducts(JSONArray selectedProducts, String title, String productFile) {
        System.out.println(
                "======================================================================================================");
        System.out.println("                    " + title);
        System.out.println(
                "======================================================================================================");
        System.out.format("| %-40s | %-10s | %-15s | %-10s | %-10s |\n", "Product Name", "Color", "Price", "Quantity",
                "Total Price");
        System.out.println(
                "======================================================================================================");
        if (selectedProducts.isEmpty()) {
            System.out.println("No " + title + " found.");
            return;
        }

        for (Object obj : selectedProducts) {
            JSONObject product = (JSONObject) obj;
            double price = Double.parseDouble(product.get("price").toString());
            int quantity = Integer.parseInt(product.get("quantity").toString());
            double totalPrice = price * quantity;

            System.out.format("| %-40s | %-10s | %-15s | %-10s | %-10s |\n", product.get("productName"),
                    product.get("color"),
                    product.get("price"), quantity, totalPrice);
        }
        System.out.println(
                "======================================================================================================");

        System.out.print("Do you want to print the receipt? (Type 'Y' to print, 'N' to skip): ");
        String userInput = scanner.next();
        if (userInput.equalsIgnoreCase("Y")) {
            printReceipt(selectedProducts);
        }
    }

    private static void printReceipt(JSONArray selectedProducts) {
        System.out.println(
                "======================================================================================================");
        System.out.println("                           RECEIPT");
        System.out.println(
                "======================================================================================================");
        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", "Product ID", "Product Name",
                "Color", "Price", "Quantity");
        System.out.println(
                "======================================================================================================");

        for (Object obj : selectedProducts) {
            JSONObject product = (JSONObject) obj;
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", product.get("productID"),
                    product.get("productName"), product.get("color"), product.get("price"),
                    product.get("quantity"));
        }
        System.out.println(
                "======================================================================================================");
        System.out.println("Receipt generated");

        double totalPrice = calculateTotalPrice(selectedProducts);
        System.out.println("Total Price: " + totalPrice);
    }

    private static double getAmountReceived(double totalPrice) {
        double amountReceived;
        do {
            try {
                System.out.print("Enter the amount received from the customer: ");
                amountReceived = Double.parseDouble(scanner.next());

                if (amountReceived < totalPrice) {
                    System.out.println(
                            "กรุณาจ่ายให้ครบทุกบาททุกสตางค์ครับ");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("จำนวนเงินเป็นเลขฮะ");
            }
        } while (true);

        return amountReceived;
    }

    private static double calculateTotalPrice(JSONArray selectedProducts) {
        double totalPrice = 0.0;
        for (Object obj : selectedProducts) {
            JSONObject product = (JSONObject) obj;
            double price = Double.parseDouble(product.get("price").toString());
            int quantity = Integer.parseInt(product.get("quantity").toString());
            totalPrice += price * quantity;
        }
        return totalPrice;
    }

    public void orderEarring() {
        JSONArray selectedProducts = new JSONArray();
        orderProduct(selectedProducts, "Earrings", EARRINGS_FILE);
    }

    public void orderRing() {
        JSONArray selectedProducts = new JSONArray();
        orderProduct(selectedProducts, "Rings", RINGS_FILE);
    }

    public void viewMenu() {
        System.out.println("Viewing menu...");
    }

    private static void displayProductList(JSONArray jsonArray) {
        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", "Product ID", "Product Name",
                "Color", "Price", "Stored Item");
        System.out.println(
                "======================================================================================================");

        for (Object obj : jsonArray) {
            JSONObject product = (JSONObject) obj;
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s |\n", product.get("productID"),
                    product.get("productName"), product.get("color"), product.get("price"),
                    product.get("storedItem"));
        }
    }

    private static JSONArray readJSONArrayFromFile(String filePath) throws Exception {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONArray) jsonParser.parse(reader);
        }
    }

    private static void saveJSONArrayToFile(JSONArray jsonArray, String filePath) throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toJSONString());
        }
    }

    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
