package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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

        // Map เพื่อเก็บยอด Quantity ของ Product ID แต่ละชิ้น
        Map<String, Integer> productQuantityMap = new HashMap<>();
        // Map เพื่อเก็บยอดขายตามวัน
        Map<String, Double> dailySalesMap = new HashMap<>();

        // แสดงข้อมูลทั้งหมดจาก receipts
        for (Object receiptObj : receipts) {
            JSONObject receipt = (JSONObject) receiptObj;
            String date = (String) receipt.get("date");
            System.out.println("Receipt ID: " + receipt.get("receiptId"));
            System.out.println("Date: " + date);
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
                String productID = (String) product.get("productID");
                String productName = (String) product.get("productName");
                int quantity = Integer.parseInt(product.get("quantity").toString());

                // บันทึกหรือเพิ่ม Quantity ใน Map
                productQuantityMap.put(productID, productQuantityMap.getOrDefault(productID, 0) + quantity);

                System.out.println("  Product ID: " + productID);
                System.out.println("  Product Name: " + productName);
                System.out.println("  Quantity: " + quantity);
                System.out.println("  Color: " + product.get("color"));
                System.out.println("  Price: " + product.get("price"));
                System.out.println("  Total Price: " + product.get("totalPrice"));
                System.out.println();
            }

            System.out.println("------------------------------");

            // เพิ่มยอดขายลงใน Map ตามวัน
            dailySalesMap.put(date, dailySalesMap.getOrDefault(date, 0.0) + receiptTotal);
        }

        // แสดง Product ID Summary ตามวัน
        System.out.println("Product ID Summary:");
        for (Map.Entry<String, Integer> entry : productQuantityMap.entrySet()) {
            System.out.println("  Date: " + entry.getKey() + ", Product ID: " + entry.getValue());
        }

        // แสดง Total Sales ตามวัน
        System.out.println("Daily Sales Summary:");
        for (Map.Entry<String, Double> entry : dailySalesMap.entrySet()) {
            System.out.println("  Date: " + entry.getKey() + ", Total Sales: " + entry.getValue());
        }

        System.out.println("Total Sales: " + totalSales);
        System.out.println("End of Receipt Data");
    }
}
