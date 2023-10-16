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
    private static final String PROMOCODE_FILE = "src/resources/json/promotionCode.json";
    private static final int MAX_PROMO_CODE_ATTEMPTS = 3;
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

            String promotionCode = null;
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

                    System.out.print("Do you want to order another " + productType
                            + "? (Type 'N' to exit, 'Y' to continue): ");
                    String userInput = scanner.next();

                    genPromocode genpromp = new genPromocode();
                    promotionCode = genpromp.generatePromotionCode();

                    if (userInput.equalsIgnoreCase("N")) {

                        continueOrdering = false;
                        break;
                    }
                }
            } while (continueOrdering);

            displaySelectedProducts(selectedProducts, "Selected " + productType, productFile);
            double amountReceived = getAmountReceived(calculateTotalPrice(selectedProducts));
            createReceipt(selectedProducts, amountReceived, promotionCode);

        } catch (Exception e) {
            System.out.println("Error ordering " + productType + ": " + e.getMessage());
        }
    }

    private static void createReceipt(JSONArray selectedProducts, double amountReceived, String promotionCode) {
        try {
            int receiptId = generateReceiptId();
            JSONObject receipt = new JSONObject();
            receipt.put("receiptId", receiptId);

            // Split the date into day, month, and year
            String[] currentDateParts = getCurrentDate().split("-");
            receipt.put("day", currentDateParts[2]);
            receipt.put("month", currentDateParts[1]);
            receipt.put("year", currentDateParts[0]);

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
            double discountAmount = 0.0;
            // Check if a promotion code is applied
            if (promotionCode != null && isValidPromotionCode(promotionCode)) {
                double discount = 0.2; // Adjust the discount percentage as needed
                discountAmount = totalPrice * discount;
                totalPrice -= discountAmount;
                receipt.put("discountAmount", discountAmount);
            }
            if (promotionCode != null) {
                removeUsedPromoCode(promotionCode);
            }

            receipt.put("products", productsArray);
            receipt.put("totalPrice", totalPrice);
            receipt.put("amountReceived", amountReceived);

            double change = amountReceived - totalPrice;
            receipt.put("change", change);

            saveReceiptToFile(receipt);

            System.out.println("Receipt created successfully!");

            displayReceiptDetails(receiptId, discountAmount, totalPrice, promotionCode);

        } catch (Exception e) {
            System.out.println("Error creating receipt: " + e.getMessage());
        }
    }

    private static void displayReceiptDetails(int receiptId, double discountAmount, double totalPrice,
            String promotionCode) {
        try {
            JSONArray receipts = readJSONArrayFromFile(RECEIPT_FILE);
            for (Object obj : receipts) {
                JSONObject receipt = (JSONObject) obj;
                if (Integer.parseInt(receipt.get("receiptId").toString()) == receiptId) {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    System.out.println(
                            "===================================================================================================================================");
                    System.out.println("Receipt ID: " + receipt.get("receiptId"));
                    System.out.println(
                            "Date: " + receipt.get("day") + "-" + receipt.get("month") + "-" + receipt.get("year"));
                    System.out.println("Time: " + receipt.get("time"));
                    System.out.println(
                            "============================================================================================================================");
                    JSONArray productsArray = (JSONArray) receipt.get("products");

                    System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s | %-10s |\n",
                            "Product ID", "Product Name", "Color", "Price", "Quantity", "Total Price");
                    System.out.println(
                            "============================================================================================================================");

                    for (Object productObj : productsArray) {
                        JSONObject product = (JSONObject) productObj;
                        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s | %-10s |\n",
                                product.get("productID"), product.get("productName"),
                                product.get("color"),
                                product.get("price"), product.get("quantity"), product.get("totalPrice"));
                    }

                    System.out.println(
                            "============================================================================================================================");
                    System.out.println("Total Price: " + receipt.get("totalPrice"));
                    System.out.println("Amount Received: " + receipt.get("amountReceived"));
                    System.out.println("Change: " + receipt.get("change"));
                    if (discountAmount > 0) {
                        System.out.println("Discount Amount: " + discountAmount);
                        System.out.println("Total Price after discount: " + totalPrice);
                    }
                    if (promotionCode != null) {
                        System.out.println("Promotion Code: " + promotionCode);
                    }
                    System.out.println(
                            "============================================================================================================================");
                    break; // หลังจากแสดงข้อมูลแล้วออกจากลูป
                }
            }
        } catch (Exception e) {
            System.out.println("Error displaying receipt details: " + e.getMessage());
        }
    }

    private static void displaySelectedProducts(JSONArray selectedProducts, String title, String productFile) {
        System.out.println(
                "======================================================================================================");
        System.out.println("                    " + title);
        System.out.println(
                "======================================================================================================");
        System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s | %-10s |\n", " Product ID", "Product Name", "Color",
                "Price", "Quantity",
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

            // เปลี่ยน productID.get("productID") เป็น product.get("productID")
            System.out.format("| %-10s | %-40s | %-10s | %-15s | %-10s | %-10s |\n", product.get("productID"),
                    product.get("productName"),
                    product.get("color"),
                    product.get("price"), quantity, totalPrice);
        }
        System.out.println(
                "======================================================================================================");

    }

    private static double getAmountReceived(double totalPrice) {
        try {
            int promoCodeAttempts = 0;
            double amountReceived = 0.0;

            do {
                try {
                    System.out.println("Total Price: " + totalPrice);

                    if (promoCodeAttempts < MAX_PROMO_CODE_ATTEMPTS) {
                        System.out.print("Do you want to use Promotion Code (Type 'N' to exit, 'Y' to continue): ");
                        String usePromo = scanner.next();

                        if (usePromo.equalsIgnoreCase("Y")) {
                            boolean validPromoCode = false;

                            for (int attempt = 1; attempt <= MAX_PROMO_CODE_ATTEMPTS; attempt++) {
                                System.out.print("Enter Promotion Code (Attempts remaining: "
                                        + (MAX_PROMO_CODE_ATTEMPTS - attempt + 1) + "): ");
                                String promotionCode = scanner.next();

                                if (isValidPromotionCode(promotionCode)) {
                                    validPromoCode = true;
                                    break;
                                } else {
                                    System.out.println("Invalid promotion code. Please try again.");
                                }
                            }

                            if (validPromoCode) {
                                double discount = 0.2; // Adjust the discount percentage as needed
                                totalPrice -= totalPrice * discount;
                                System.out.println("Promotion Code applied! Total Price after discount: " + totalPrice);
                            }
                        }
                    }

                    while (true) {
                        System.out.print("Enter the amount received from the customer: ");
                        amountReceived = Double.parseDouble(scanner.next());

                        if (promoCodeAttempts > 0) {
                            System.out.println("Discounted Total Price: " + totalPrice);
                        }

                        return amountReceived;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error getting amount received: " + e.getMessage());
            return 0.0; // ควรทำการ handle และรีเทิร์นค่าที่เหมาะสม
        }
    }

    private static boolean isValidPromotionCode(String inputCode) {
        try {
            JSONArray promotionCodes = readJSONArrayFromFile(PROMOCODE_FILE);
            for (Object obj : promotionCodes) {
                JSONObject promoCode = (JSONObject) obj;
                if (promoCode.containsKey("promotionCode") && promoCode.get("promotionCode") != null) {
                    String code = promoCode.get("promotionCode").toString();
                    if (code.equals(inputCode)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading promotion codes: " + e.getMessage());
        }
        return false;
    }

    private static void removeUsedPromoCode(String promotionCode) {
        try {
            JSONArray promotionCodes = readJSONArrayFromFile(PROMOCODE_FILE);
            JSONArray updatedPromoCodes = new JSONArray();

            for (Object obj : promotionCodes) {
                JSONObject promoCode = (JSONObject) obj;
                if (!promoCode.containsKey("promotionCode") || promoCode.get("promotionCode") == null) {
                    continue;
                }

                String code = promoCode.get("promotionCode").toString();
                if (!code.equals(promotionCode)) {
                    updatedPromoCodes.add(promoCode);
                }
            }

            try (FileWriter fileWriter = new FileWriter(PROMOCODE_FILE)) {
                fileWriter.write(updatedPromoCodes.toJSONString());
            }
        } catch (Exception e) {
            System.out.println("Error removing used promo code: " + e.getMessage());
        }
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

    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
