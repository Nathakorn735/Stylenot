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

public class Receipt {
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";
    private static final Scanner scanner = new Scanner(System.in);

    public void Receipt() {
        System.out.print("Enter the date (yyyy-MM-dd) or month (yyyy-MM) or year (yyyy) to show sales data: ");
        String inputDate = scanner.nextLine();

        try {
            JSONArray receipts = readJSONArrayFromFile(RECEIPT_FILE);

            for (Object obj : receipts) {
                JSONObject receipt = (JSONObject) obj;
                String receiptDate = receipt.get("year") + "-" + receipt.get("month") + "-" + receipt.get("day");

                if (receiptDate.startsWith(inputDate)) {
                    displayReceiptDetails(receipt);
                }
            }

        } catch (IOException | ParseException e) {
            System.out.println("Error showing receipt data: " + e.getMessage());
        }
    }

    private void displayReceiptDetails(JSONObject receipt) {
        System.out.println("========================");
        System.out.println("Receipt ID: " + receipt.get("receiptId"));
        System.out.println("========================");
        System.out.println("Date: " + receipt.get("year") + "-" + receipt.get("month") + "-" + receipt.get("day"));
        System.out.println("Time: " + receipt.get("time"));
        System.out.println("Total Price: " + receipt.get("totalPrice"));
        System.out.println("Amount Received: " + receipt.get("amountReceived"));
        System.out.println("Change: " + receipt.get("change"));
        JSONArray products = (JSONArray) receipt.get("products");

        for (Object productObj : products) {
            JSONObject product = (JSONObject) productObj;
            System.out.println("Product ID: " + product.get("productID"));
            System.out.println("Product Name: " + product.get("productName"));
            System.out.println("Quantity: " + product.get("quantity"));
            System.out.println("Color: " + product.get("color"));
            System.out.println("Price: " + product.get("price"));
            System.out.println("Total Price: " + product.get("totalPrice"));
            System.out.println("------------------------");
        }

        System.out.println(" ");
    }

    private static JSONArray readJSONArrayFromFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONArray) jsonParser.parse(reader);
        }
    }

    public static void main(String[] args) {
        new Receipt().Receipt();
        scanner.close();
    }
}
