package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class SalesReport {
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";

    public SalesReport() {

    }

    public void showReceiptData() {
        try {
            // อ่านข้อมูลจากไฟล์ JSON
            JSONArray receipts = readJsonFile(RECEIPT_FILE);

            // เรียกใช้เมธอดแสดงข้อมูล receipt
            displayReceiptData(receipts);
        } catch (Exception e) {
            System.out.println("An error occurred while showing the receipt data: " + e.getMessage());
        }
    }

    private JSONArray readJsonFile(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filePath)) {
            return (JSONArray) parser.parse(reader);
        }
    }

    private void displayReceiptData(JSONArray receipts) {
        double totalSales = 0;

        // แสดงข้อมูลทั้งหมดจาก receipts
        for (Object receiptObj : receipts) {
            JSONObject receipt = (JSONObject) receiptObj;
            System.out.println("Receipt ID: " + receipt.get("receiptId"));
            System.out.println("Date: " + receipt.get("date"));
            System.out.println("Time: " + receipt.get("time"));
            System.out.println("Total Price: " + receipt.get("totalPrice"));
            System.out.println("Amount Received: " + receipt.get("amountReceived"));
            System.out.println("Change: " + receipt.get("change"));

            double receiptTotal = Double.parseDouble(receipt.get("totalPrice").toString());
            totalSales += receiptTotal;

            JSONArray products = (JSONArray) receipt.get("products");
            System.out.println("Products:");

            for (Object productObj : products) {
                JSONObject product = (JSONObject) productObj;
                System.out.println("  Product ID: " + product.get("productID"));
                System.out.println("  Product Name: " + product.get("productName"));
                System.out.println("  Quantity: " + product.get("quantity"));
                System.out.println("  Color: " + product.get("color"));
                System.out.println("  Price: " + product.get("price"));
                System.out.println("  Total Price: " + product.get("totalPrice"));
                System.out.println();
            }

            System.out.println("------------------------------");
        }

        System.out.println("Total Sales: " + totalSales);
        System.out.println("End of Receipt Data");
    }

}
