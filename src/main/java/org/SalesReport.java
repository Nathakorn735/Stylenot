package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SalesReport {
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";
    private static final Scanner scanner = new Scanner(System.in);

    public SalesReport() {
    }

    public void showReceiptData() {
        boolean continueProcessing = true;

        while (continueProcessing) {
            String inputDate;

            while (true) {
                System.out.print("Enter the date (yyyy-MM-dd) or month (yyyy-MM) or year (yyyy) to show sales data: ");
                inputDate = scanner.nextLine();


                if (inputDate.matches("\\d{4}-\\d{2}-\\d{2}|\\d{4}-\\d{2}|\\d+")) {

                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid date format (yyyy-MM-dd) or a numeric value.");
                }
            }

            try {
                JSONArray receipts = readJSONArrayFromFile(RECEIPT_FILE);

                double totalAmount = 0.0;
                Map<String, Integer> productQuantities = new HashMap<>();
                Map<String, Double> productTotalPrices = new HashMap<>();

                for (Object obj : receipts) {
                    JSONObject receipt = (JSONObject) obj;
                    String receiptDate = receipt.get("year") + "-" + receipt.get("month") + "-" + receipt.get("day");

                    if (receiptDate.startsWith(inputDate)) {
                        JSONArray products = (JSONArray) receipt.get("products");
                        for (Object productObj : products) {
                            JSONObject product = (JSONObject) productObj;
                            double totalPrice = Double.parseDouble(product.get("totalPrice").toString());
                            int quantity = Integer.parseInt(product.get("quantity").toString());
                            String productID = product.get("productID").toString();

                            totalAmount += totalPrice;


                            productQuantities.put(productID, productQuantities.getOrDefault(productID, 0) + quantity);


                            productTotalPrices.put(productID, productTotalPrices.getOrDefault(productID, 0.0) + totalPrice);
                        }
                    }
                }


                System.out.println(
                        "===============================================");
                System.out.println("Total Quantity and Total Price for each product");
                System.out.println(
                        "===============================================");
                for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
                    String productID = entry.getKey();
                    int totalQuantity = entry.getValue();
                    double totalProductPrice = productTotalPrices.get(productID);

                    System.out.println("Product ID: " + productID + ", Total Quantity: " + totalQuantity + ", Total Price: " + totalProductPrice);
                }
                System.out.println(
                        "===============================================");
                System.out.println("Total Amount: " + totalAmount);
                System.out.println(
                        "===============================================");

                System.out.print("Do you want to process another transaction? (y/n): ");
                String continueOption = scanner.nextLine().trim().toLowerCase();
                continueProcessing = continueOption.equals("y");

            } catch (IOException | ParseException e) {
                System.out.println("Error showing receipt data: " + e.getMessage());
            }
        }
    }

    private static JSONArray readJSONArrayFromFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONArray) jsonParser.parse(reader);
        }
    }

    public static void main(String[] args) {
        new SalesReport().showReceiptData();
        scanner.close();
    }
}
