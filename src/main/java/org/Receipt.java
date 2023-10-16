package main.java.org;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Receipt {
    private static final String RECEIPT_FILE = "src/resources/json/receipt.json";
    private static final Scanner scanner = new Scanner(System.in);

    public void Receipts() {
        char continueOption;
        do {
            String inputDate;

            while (true) {
                System.out.print("Enter the date (yyyy-MM-dd) or month (yyyy-MM) or year (yyyy) to show sales data: ");
                inputDate = scanner.nextLine();

                // ตรวจสอบว่า inputDate เป็นตัวเลขหรือมีรูปแบบที่ถูกต้อง (รับ - ได้)
                if (inputDate.matches("\\d{4}-\\d{2}-\\d{2}|\\d{4}-\\d{2}|\\d+")) {
                    // ถ้าเป็นรูปแบบที่ถูกต้องให้ทำงานต่อ
                    break;
                } else {
                    System.out.println("Invalid input. Please enter a valid date format (yyyy-MM-dd) or a numeric value.");
                }
            }

            try {
                JSONArray receipts = readJSONArrayFromFile(RECEIPT_FILE);

                boolean foundData = false; // เพิ่มตัวแปรนี้
                for (Object obj : receipts) {
                    JSONObject receipt = (JSONObject) obj;
                    String receiptDate = receipt.get("year") + "-" + receipt.get("month") + "-" + receipt.get("day");

                    if (receiptDate.startsWith(inputDate)) {
                        foundData = true; // ถ้ามีข้อมูลที่ตรงตามเงื่อนไข
                        displayReceiptDetails(receipt);
                    }
                }

                if (!foundData) {
                    System.out.println("No data found for the entered date: " + inputDate);
                }

            } catch (IOException | ParseException e) {
                System.out.println("Error showing receipt data: " + e.getMessage());
            }

            System.out.print("Do you want to show receipts for another date? (y/n): ");
            continueOption = scanner.next().charAt(0);
            scanner.nextLine(); // consume newline character
        } while (Character.toLowerCase(continueOption) == 'y');
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
        new Receipt().Receipts();
        scanner.close();
    }
}
